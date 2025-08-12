/*-----------------------------------------------------------------------------
 *
 *
 *  Copyright (C) 2025 Frenkel Smeijers
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 *  02111-1307, USA.
 *
 * DESCRIPTION:
 *      libdoom driver Java version
 *
 *-----------------------------------------------------------------------------*/

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class LibDoomDriver {

	private static final int SCREENWIDTH = 320;
	private static final int SCREENHEIGHT = 200;

	private final Arena arena;
	private final SymbolLookup libdoom;
	private final Linker linker;

	private LibDoomPanel libdoompanel;
	private final Queue<KeyEvent> keyboardQueue = new ConcurrentLinkedDeque<>();
	private Sequencer midiSequencer;

	public LibDoomDriver() {
		this.arena = Arena.global();

		String os = System.getProperty("os.name");
		Path path;
		if ("Linux".equals(os)) {
			path = Path.of("../linuxdoom-1.10/linux", "libdoom.so");
		} else if (os.startsWith("Windows")) {
			path = Path.of("../linuxdoom-1.10/windows", "libdoom.dll");
		} else {
			throw new IllegalStateException("Unsupported operating system: " + os);
		}
		this.libdoom = SymbolLookup.libraryLookup(path, arena);

		this.linker = Linker.nativeLinker();
	}

	@FunctionalInterface
	private interface MemorySegmentConsumer {
		void accept(MemorySegment memorySegment);
	}

	@FunctionalInterface
	private interface LibDoomRunnable {
		void run();
	}

	private MemorySegment allocate(MemorySegmentConsumer func, long elementCount)
			throws NoSuchMethodException, IllegalAccessException {
		FunctionDescriptor function = FunctionDescriptor.ofVoid(
				ValueLayout.ADDRESS.withTargetLayout(MemoryLayout.sequenceLayout(elementCount, ValueLayout.JAVA_BYTE)));
		MethodHandle methodHandle = MethodHandles.lookup().findVirtual(MemorySegmentConsumer.class, "accept",
				function.toMethodType());
		MethodHandle target = methodHandle.bindTo(func);
		return linker.upcallStub(target, function, arena);
	}

	private MemorySegment allocate(LibDoomRunnable func) throws NoSuchMethodException, IllegalAccessException {
		FunctionDescriptor function = FunctionDescriptor.ofVoid();
		MethodHandle methodHandle = MethodHandles.lookup().findVirtual(LibDoomRunnable.class, "run",
				function.toMethodType());
		MethodHandle target = methodHandle.bindTo(func);
		return linker.upcallStub(target, function, arena);
	}

	private void error(MemorySegment error) {
		System.err.println(error.getString(0));
		System.exit(1);
	}

	private void initGraphics() {
		this.libdoompanel = new LibDoomPanel();

		JFrame frame = new JFrame("libdoom");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.add(libdoompanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);

		frame.setFocusTraversalKeysEnabled(false);
		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent keyEvent) {
				keyboardQueue.add(keyEvent);
			}

			@Override
			public void keyPressed(KeyEvent keyEvent) {
				keyboardQueue.add(keyEvent);
			}
		});
	}

	private int xlatekey(int keyCode) {
		return switch (keyCode) {
		case KeyEvent.VK_RIGHT -> 0xae;
		case KeyEvent.VK_LEFT -> 0xac;
		case KeyEvent.VK_UP -> 0xad;
		case KeyEvent.VK_DOWN -> 0xaf;
		case KeyEvent.VK_ESCAPE -> 27;
		case KeyEvent.VK_ENTER -> 13;
		case KeyEvent.VK_TAB -> 9;
		case KeyEvent.VK_F1 -> (0x80 + 0x3b);
		case KeyEvent.VK_F2 -> (0x80 + 0x3c);
		case KeyEvent.VK_F3 -> (0x80 + 0x3d);
		case KeyEvent.VK_F4 -> (0x80 + 0x3e);
		case KeyEvent.VK_F5 -> (0x80 + 0x3f);
		case KeyEvent.VK_F6 -> (0x80 + 0x40);
		case KeyEvent.VK_F7 -> (0x80 + 0x41);
		case KeyEvent.VK_F8 -> (0x80 + 0x42);
		case KeyEvent.VK_F9 -> (0x80 + 0x43);
		case KeyEvent.VK_F10 -> (0x80 + 0x44);
		case KeyEvent.VK_F11 -> (0x80 + 0x57);
		case KeyEvent.VK_F12 -> (0x80 + 0x58);
		case KeyEvent.VK_BACK_SPACE -> 127;
		case KeyEvent.VK_PAUSE -> 0xff;
		case KeyEvent.VK_EQUALS -> 0x3d;
		case KeyEvent.VK_MINUS -> 0x2d;
		case KeyEvent.VK_SHIFT -> (0x80 + 0x36);
		case KeyEvent.VK_CONTROL -> (0x80 + 0x1d);
		case KeyEvent.VK_ALT -> (0x80 + 0x38);
		case KeyEvent.VK_ALT_GRAPH -> (0x80 + 0x38);
		default -> {
			if ('A' <= keyCode && keyCode <= 'Z') {
				yield keyCode - 'A' + 'a';
			} else {
				yield keyCode;
			}
		}
		};
	}

	private void startTic() {
		while (!keyboardQueue.isEmpty()) {
			KeyEvent keyEvent = keyboardQueue.poll();
			int type = KeyEvent.KEY_RELEASED == keyEvent.getID() ? 1 : 0;
			int data1 = xlatekey(keyEvent.getKeyCode());
			postEvent(type, data1);
		}
	}

	private void postEvent(int type, int data1) {
		try {
			MemorySegment address = libdoom.findOrThrow("L_PostEvent");
			FunctionDescriptor function = FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT);
			MethodHandle methodHandle = linker.downcallHandle(address, function);
			methodHandle.invokeExact(type, data1);
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	private void setPalette(MemorySegment palette) {
		byte[] bytes = palette.toArray(ValueLayout.JAVA_BYTE);
		libdoompanel.setPalette(bytes);
	}

	private void finishUpdate(MemorySegment src) {
		byte[] bytes = src.toArray(ValueLayout.JAVA_BYTE);
		libdoompanel.blitBuffer(bytes);
	}

	private boolean isSoundAvailable() {
		try {
			AudioSystem.getClip().close();
			return true;
		} catch (LineUnavailableException | IllegalArgumentException _) {
			System.err.println("Sound is unavailable");
			return false;
		}
	}

	private boolean isMusicAvailable() {
		try {
			this.midiSequencer = MidiSystem.getSequencer();
			return true;
		} catch (MidiUnavailableException _) {
			System.err.println("Music is unavailable");
			return false;
		}
	}

	private void startSound(MemorySegment memorySegment) {
		byte[] dmxBytes = memorySegment.toArray(ValueLayout.JAVA_BYTE);

		ByteBuffer bb = ByteBuffer.wrap(dmxBytes);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.getShort(); // format number (must be 3)
		short sampleRate = bb.getShort(); // usually 11025
		int numberOfSamples = Math.min(bb.getInt(), dmxBytes.length - 8);
		AudioFormat audioFormat = new AudioFormat(sampleRate, 8, 1, false, false);

		try {
			Clip clip = AudioSystem.getClip();
			clip.addLineListener(event -> {
				if (LineEvent.Type.STOP == event.getType()) {
					clip.close();
				}

			});
			clip.open(audioFormat, dmxBytes, 0x18, numberOfSamples - 16 - 16);
			clip.start();
		} catch (LineUnavailableException e) {
			throw new IllegalStateException(e);
		}
	}

	private void playSong(MemorySegment memorySegment) {
		try {
			midiSequencer.close();

			InputStream midi = new ByteArrayInputStream(memorySegment.toArray(ValueLayout.JAVA_BYTE));
			midiSequencer.setSequence(MidiSystem.getSequence(midi));
			midiSequencer.open();
			midiSequencer.start();
		} catch (MidiUnavailableException | InvalidMidiDataException | IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void setFunc(String name, LibDoomRunnable func) {
		try {
			MemorySegment address = libdoom.findOrThrow(name);
			FunctionDescriptor function = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
			MethodHandle methodHandle = linker.downcallHandle(address, function);
			methodHandle.invokeExact(allocate(func));
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	private void setFunc(String name, MemorySegmentConsumer func, long elementCount) {
		try {
			MemorySegment address = libdoom.findOrThrow(name);
			FunctionDescriptor function = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
			MethodHandle methodHandle = linker.downcallHandle(address, function);
			methodHandle.invokeExact(allocate(func, elementCount));
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	private void setMyArgs(List<String> arguments) {
		int argc = arguments.size();
		MemorySegment argv = arena.allocate(ValueLayout.ADDRESS, argc);
		int i = 0;
		for (String argument : arguments) {
			argv.setAtIndex(ValueLayout.ADDRESS, i, arena.allocateFrom(argument));
			i++;
		}

		try {
			MemorySegment address = libdoom.findOrThrow("L_SetMyArgs");
			FunctionDescriptor function = FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
			MethodHandle methodHandle = linker.downcallHandle(address, function);
			methodHandle.invokeExact(argc, argv);
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	private void doomMain() {
		try {
			MemorySegment address = libdoom.findOrThrow("L_DoomMain");
			FunctionDescriptor function = FunctionDescriptor.ofVoid();
			MethodHandle methodHandle = linker.downcallHandle(address, function);
			methodHandle.invokeExact();
		} catch (Throwable t) {
			throw new IllegalStateException(t);
		}
	}

	public void doom(String[] args) {
		setFunc("L_SetErrorFunc", this::error, 80);
		setFunc("L_SetInitGraphicsFunc", this::initGraphics);
		setFunc("L_SetSetPaletteFunc", this::setPalette, 3 * 256);
		setFunc("L_SetFinishUpdateFunc", this::finishUpdate, SCREENWIDTH * SCREENHEIGHT);
		setFunc("L_SetStartTicFunc", this::startTic);

		MemorySegmentConsumer startSoundFunc = isSoundAvailable() ? this::startSound : _ -> {
		};
		MemorySegmentConsumer playSongFunc = isMusicAvailable() ? this::playSong : _ -> {
		};
		setFunc("L_SetStartSoundFunc", startSoundFunc, 57072); // size of DSBOSSIT, the largest sound lump
		setFunc("L_SetPlaySongFunc", playSongFunc, 81574); // size of D_DDTBL2 and D_DDTBL3,
															// the largest music files in MIDI format

		List<String> arguments = new ArrayList<>();
		arguments.add("");
		arguments.addAll(Arrays.asList(args));
		setMyArgs(arguments);
		doomMain();
	}

	public static void main(String[] args) {
		LibDoomDriver libdoomdriver = new LibDoomDriver();
		libdoomdriver.doom(args);
	}

	private static class LibDoomPanel extends JPanel {

		private static final long serialVersionUID = 5193832661212316322L;

		private BufferedImage img;
		private byte[] buffer = new byte[SCREENWIDTH * SCREENHEIGHT];

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(SCREENWIDTH, SCREENHEIGHT);
		}

		@Override
		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, this);
		}

		private void setPalette(byte[] bytes) {
			IndexColorModel colorModel = new IndexColorModel(8, 256, bytes, 0, false);
			DataBufferByte dataBuffer = new DataBufferByte(buffer, buffer.length);
			WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, SCREENWIDTH, SCREENHEIGHT, SCREENWIDTH,
					1, new int[] { 0 }, null);
			this.img = new BufferedImage(colorModel, raster, false, null);
		}

		private void blitBuffer(byte[] bytes) {
			System.arraycopy(bytes, 0, buffer, 0, buffer.length);
			repaint();
		}
	}

}
