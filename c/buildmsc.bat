mkdir windows

@set CFLAGS=/O2 /GL

@set GLOBOBJS=
@set GLOBOBJS=%GLOBOBJS% i_driver.c

@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/am_map.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/doomdef.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/doomstat.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/dstrings.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/d_items.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/d_main.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/d_net.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/f_finale.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/f_wipe.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/g_game.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/hu_lib.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/hu_stuff.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/info.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/i_lib.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/m_argv.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/m_bbox.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/m_cheat.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/m_fixed.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/m_menu.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/m_misc.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/m_random.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/m_swap.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/memio.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/mus2mid.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_ceilng.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_doors.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_enemy.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_floor.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_inter.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_lights.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_map.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_maputl.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_mobj.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_plats.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_pspr.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_saveg.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_setup.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_sight.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_spec.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_switch.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_telept.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_tick.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/p_user.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/r_bsp.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/r_data.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/r_draw.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/r_main.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/r_plane.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/r_segs.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/r_sky.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/r_things.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/sounds.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/st_lib.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/st_stuff.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/s_sound.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/tables.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/v_video.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/wi_stuff.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/w_wad.c
@set GLOBOBJS=%GLOBOBJS% ../linuxdoom-1.10/z_zone.c

cl %GLOBOBJS% %CFLAGS% /I../linuxdoom-1.10 /Fewindows/libdoom.exe

del *.err
del *.obj
