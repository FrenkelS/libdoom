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

#include <stdarg.h>
#include <stdlib.h>
#include <time.h>

#include "i_lib.h"
#include "i_net.h"
#include "i_sound.h"
#include "i_system.h"
#include "i_video.h"

#include "d_main.h"
#include "m_argv.h"
#include "v_video.h"
#include "w_wad.h"


#define IMPLEMENT_ME() I_Error("Implement me: %s: %s @ %i\n", __FILE__, __func__ , __LINE__)


#if defined _MSC_VER
__declspec(noreturn) static void __builtin_unreachable(void)
{
	__assume(false);
}
#endif


//**************************************************************************************
//
// Exit code
//

void (*errorFunc)(char*);


DllExport void L_SetErrorFunc(void(*func)(char*))
{
	errorFunc = func;
}


void I_Error(char *error, ...)
{
	char errorMessage[80];
	va_list argptr;

	va_start(argptr, error);
	vsnprintf(errorMessage, sizeof(errorMessage), error, argptr);
	va_end(argptr);

	errorFunc(errorMessage);
	__builtin_unreachable();
}


void I_Quit(void)
{
	IMPLEMENT_ME();
}


//**************************************************************************************
//
// Miscellaneous code
//

void I_Init(void)
{
}


void I_StartFrame(void)
{
}


void I_StartTic(void)
{
}


ticcmd_t* I_BaseTiccmd(void)
{
	static ticcmd_t emptycmd;
	return &emptycmd;
}


void I_Tactile(int on, int off, int total)
{
}


DllExport void L_SetMyArgs(int argc, char** argv)
{
	myargc = argc;
	myargv = argv;
}


DllExport void L_DoomMain(void)
{
	D_DoomMain();
}


//**************************************************************************************
//
// Memory code
//

byte* I_AllocLow(int length)
{
	byte* ptr = malloc(length);
	memset(ptr, 0, length);
	return ptr;
}


byte* I_ZoneBase(int *size)
{
	*size = 8 * 1024 * 1024;
	return malloc(*size);
}


//**************************************************************************************
//
// Network code
//

void I_InitNetwork(void)
{
	// single player game
	netgame = false;

	doomcom = malloc(sizeof(*doomcom));
	memset(doomcom, 0, sizeof(*doomcom));

	doomcom->id            = DOOMCOM_ID;
	doomcom->numnodes      = 1;
	doomcom->ticdup        = 1;
	doomcom->extratics     = 0;
	doomcom->deathmatch    = false;
	doomcom->consoleplayer = 0;
	doomcom->numplayers    = 1;
}


void I_NetCmd(void)
{
	IMPLEMENT_ME();
}


//**************************************************************************************
//
// Sound code
//

void I_SetChannels()
{
}


void I_SetMusicVolume(int volume)
{
}


int I_RegisterSong(void *data)
{
	return 0;
}


void I_PlaySong(int handle, int looping)
{
}


void I_SubmitSound(void)
{
}


void I_StopSong(int handle)
{
}


void I_UnRegisterSong(int handle)
{
}


int I_GetSfxLumpNum(sfxinfo_t* sfxinfo)
{
	char namebuf[9];
	sprintf(namebuf, "ds%s", sfxinfo->name);
	return W_GetNumForName(namebuf);
}


int I_StartSound(int id, int vol, int sep, int pitch, int priority)
{
	return id;
}


int I_SoundIsPlaying(int handle)
{
	return 0;
}


void I_StopSound(int handle)
{
	IMPLEMENT_ME();
}


void I_PauseSong(int handle)
{
	IMPLEMENT_ME();
}


void I_ResumeSong(int handle)
{
	IMPLEMENT_ME();
}


void I_UpdateSoundParams(int handle, int vol, int sep, int pitch)
{
	IMPLEMENT_ME();
}


//**************************************************************************************
//
// Graphics code
//

void (*initGraphicsFunc)(void);


DllExport void L_SetInitGraphicsFunc(void(*func)(void))
{
	initGraphicsFunc = func;
}


void I_InitGraphics(void)
{
	initGraphicsFunc();
}


void I_UpdateNoBlit(void)
{
}


void (*setPaletteFunc)(unsigned char*);


DllExport void L_SetSetPaletteFunc(void(*func)(unsigned char*))
{
	setPaletteFunc = func;
}


void I_SetPalette(byte* palette)
{
	setPaletteFunc(palette);
}


void (*finishUpdateFunc)(unsigned char*);


DllExport void L_SetFinishUpdateFunc(void(*func)(unsigned char*))
{
	finishUpdateFunc = func;
}


void I_FinishUpdate(void)
{
	finishUpdateFunc(screens[0]);
}


void I_ReadScreen(byte* scr)
{
	memcpy(scr, screens[0], SCREENWIDTH * SCREENHEIGHT);
}


void I_WaitVBL(int count)
{
	IMPLEMENT_ME();
}


//**************************************************************************************
//
// Time code
//

int I_GetTime(void)
{
	clock_t c;
	static clock_t basetime = 0;

	c = clock();
	if (!basetime)
		basetime = c;
	return (c - basetime) * TICRATE / CLOCKS_PER_SEC;
}
