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
 *      libdoom dummy driver to see if everything compiles
 *
 *-----------------------------------------------------------------------------*/

#include <stdio.h>
#include <stdlib.h>

#include "i_lib.h"


static void Error(char *error)
{
	printf("%s\n", error);
	exit(1);
}


static void noopRunnable(void)
{
}


static void noopConsumer(unsigned char *bytes)
{
}


int main(int argc, char** argv)
{
	L_SetErrorFunc(Error);

	L_SetInitGraphicsFunc(noopRunnable);
	L_SetSetPaletteFunc(noopConsumer);
	L_SetFinishUpdateFunc(noopConsumer);
	L_SetStartTicFunc(noopRunnable);
	L_SetStartSoundFunc(noopConsumer);
	L_SetPlaySongFunc(noopConsumer);

	L_SetMyArgs(argc, argv);
	L_DoomMain();

	return 0;
}
