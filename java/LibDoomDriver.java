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
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LibDoomDriver {

	private static final int SCREENWIDTH = 320;
	private static final int SCREENHEIGHT = 200;

	private final Arena arena;
	private final SymbolLookup libdoom;
	private final Linker linker;

	private LibDoomPanel libdoompanel;

	public LibDoomDriver() {
		this.arena = Arena.global();

		Path path = Path.of("../linuxdoom-1.10/windows", "libdoom.dll");
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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(libdoompanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private void setPalette(MemorySegment palette) {
		byte[] bytes = palette.toArray(ValueLayout.JAVA_BYTE);
		libdoompanel.setPalette(bytes);
	}

	private void finishUpdate(MemorySegment src) {
		byte[] bytes = src.toArray(ValueLayout.JAVA_BYTE);
		libdoompanel.setPixels(bytes);
	}

	private void setErrorFunc(MemorySegmentConsumer func) throws Throwable {
		MemorySegment address = libdoom.findOrThrow("L_SetErrorFunc");
		FunctionDescriptor function = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
		MethodHandle methodHandle = linker.downcallHandle(address, function);
		methodHandle.invokeExact(allocate(func, 80));
	}

	private void setInitGraphicsFunc(LibDoomRunnable func) throws Throwable {
		MemorySegment address = libdoom.findOrThrow("L_SetInitGraphicsFunc");
		FunctionDescriptor function = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
		MethodHandle methodHandle = linker.downcallHandle(address, function);
		methodHandle.invokeExact(allocate(func));
	}

	private void setSetPaletteFunc(MemorySegmentConsumer func) throws Throwable {
		MemorySegment address = libdoom.findOrThrow("L_SetSetPaletteFunc");
		FunctionDescriptor function = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
		MethodHandle methodHandle = linker.downcallHandle(address, function);
		methodHandle.invokeExact(allocate(func, 3 * 256));
	}

	private void setFinishUpdateFunc(MemorySegmentConsumer func) throws Throwable {
		MemorySegment address = libdoom.findOrThrow("L_SetFinishUpdateFunc");
		FunctionDescriptor function = FunctionDescriptor.ofVoid(ValueLayout.ADDRESS);
		MethodHandle methodHandle = linker.downcallHandle(address, function);
		methodHandle.invokeExact(allocate(func, SCREENWIDTH * SCREENHEIGHT));
	}

	private void setMyArgs(List<String> arguments) throws Throwable {
		int argc = arguments.size();
		MemorySegment argv = arena.allocate(ValueLayout.ADDRESS, argc);
		int i = 0;
		for (String argument : arguments) {
			argv.setAtIndex(ValueLayout.ADDRESS, i, arena.allocateFrom(argument));
			i++;
		}

		MemorySegment address = libdoom.findOrThrow("L_SetMyArgs");
		FunctionDescriptor function = FunctionDescriptor.ofVoid(ValueLayout.JAVA_INT, ValueLayout.ADDRESS);
		MethodHandle methodHandle = linker.downcallHandle(address, function);
		methodHandle.invokeExact(argc, argv);
	}

	private void doomMain() throws Throwable {
		MemorySegment address = libdoom.findOrThrow("L_DoomMain");
		FunctionDescriptor function = FunctionDescriptor.ofVoid();
		MethodHandle methodHandle = linker.downcallHandle(address, function);
		methodHandle.invokeExact();
	}

	public void doom(String[] args) throws Throwable {
		setErrorFunc(this::error);
		setInitGraphicsFunc(this::initGraphics);
		setSetPaletteFunc(this::setPalette);
		setFinishUpdateFunc(this::finishUpdate);

		List<String> arguments = new ArrayList<>();
		arguments.add("");
		arguments.addAll(Arrays.asList(args));
		setMyArgs(arguments);
		doomMain();
	}

	public static void main(String[] args) throws Throwable {
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

		private void setPixels(byte[] bytes) {
			System.arraycopy(bytes, 0, buffer, 0, buffer.length);
			repaint();
		}
	}

}
