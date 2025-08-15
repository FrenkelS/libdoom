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
 *      libdoom code
 *
 *-----------------------------------------------------------------------------*/

#ifndef __I_LIB__
#define __I_LIB__


#if !defined DllExport
#define DllExport
#endif


DllExport void L_SetErrorFunc(void(*func)(char*));
DllExport void L_SetInitGraphicsFunc(void(*func)(void));
DllExport void L_SetSetPaletteFunc(void(*func)(unsigned char*));
DllExport void L_SetFinishUpdateFunc(void(*func)(unsigned char*));
DllExport void L_SetStartTicFunc(void(*func)(void));
DllExport void L_SetStartSoundFunc(void(*func)(unsigned char*));
DllExport void L_SetPlaySongFunc(void(*func)(unsigned char*));

DllExport void L_PostEvent(int type, int data1, int data2);
DllExport void L_SetMyArgs(int argc, char** argv);
DllExport void L_DoomMain(void);

#endif
