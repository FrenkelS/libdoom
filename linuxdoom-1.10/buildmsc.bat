mkdir windows

@set CFLAGS=/O2 /GL

@set GLOBOBJS=
@rem @set GLOBOBJS=%GLOBOBJS% i_driver.c

@set GLOBOBJS=%GLOBOBJS% am_map.c
@set GLOBOBJS=%GLOBOBJS% doomdef.c
@set GLOBOBJS=%GLOBOBJS% doomstat.c
@set GLOBOBJS=%GLOBOBJS% dstrings.c
@set GLOBOBJS=%GLOBOBJS% d_items.c
@set GLOBOBJS=%GLOBOBJS% d_main.c
@set GLOBOBJS=%GLOBOBJS% d_net.c
@set GLOBOBJS=%GLOBOBJS% f_finale.c
@set GLOBOBJS=%GLOBOBJS% f_wipe.c
@set GLOBOBJS=%GLOBOBJS% g_game.c
@set GLOBOBJS=%GLOBOBJS% hu_lib.c
@set GLOBOBJS=%GLOBOBJS% hu_stuff.c
@set GLOBOBJS=%GLOBOBJS% info.c
@set GLOBOBJS=%GLOBOBJS% i_lib.c
@set GLOBOBJS=%GLOBOBJS% m_argv.c
@set GLOBOBJS=%GLOBOBJS% m_bbox.c
@set GLOBOBJS=%GLOBOBJS% m_cheat.c
@set GLOBOBJS=%GLOBOBJS% m_fixed.c
@set GLOBOBJS=%GLOBOBJS% m_menu.c
@set GLOBOBJS=%GLOBOBJS% m_misc.c
@set GLOBOBJS=%GLOBOBJS% m_random.c
@set GLOBOBJS=%GLOBOBJS% m_swap.c
@set GLOBOBJS=%GLOBOBJS% p_ceilng.c
@set GLOBOBJS=%GLOBOBJS% p_doors.c
@set GLOBOBJS=%GLOBOBJS% p_enemy.c
@set GLOBOBJS=%GLOBOBJS% p_floor.c
@set GLOBOBJS=%GLOBOBJS% p_inter.c
@set GLOBOBJS=%GLOBOBJS% p_lights.c
@set GLOBOBJS=%GLOBOBJS% p_map.c
@set GLOBOBJS=%GLOBOBJS% p_maputl.c
@set GLOBOBJS=%GLOBOBJS% p_mobj.c
@set GLOBOBJS=%GLOBOBJS% p_plats.c
@set GLOBOBJS=%GLOBOBJS% p_pspr.c
@set GLOBOBJS=%GLOBOBJS% p_saveg.c
@set GLOBOBJS=%GLOBOBJS% p_setup.c
@set GLOBOBJS=%GLOBOBJS% p_sight.c
@set GLOBOBJS=%GLOBOBJS% p_spec.c
@set GLOBOBJS=%GLOBOBJS% p_switch.c
@set GLOBOBJS=%GLOBOBJS% p_telept.c
@set GLOBOBJS=%GLOBOBJS% p_tick.c
@set GLOBOBJS=%GLOBOBJS% p_user.c
@set GLOBOBJS=%GLOBOBJS% r_bsp.c
@set GLOBOBJS=%GLOBOBJS% r_data.c
@set GLOBOBJS=%GLOBOBJS% r_draw.c
@set GLOBOBJS=%GLOBOBJS% r_main.c
@set GLOBOBJS=%GLOBOBJS% r_plane.c
@set GLOBOBJS=%GLOBOBJS% r_segs.c
@set GLOBOBJS=%GLOBOBJS% r_sky.c
@set GLOBOBJS=%GLOBOBJS% r_things.c
@set GLOBOBJS=%GLOBOBJS% sounds.c
@set GLOBOBJS=%GLOBOBJS% st_lib.c
@set GLOBOBJS=%GLOBOBJS% st_stuff.c
@set GLOBOBJS=%GLOBOBJS% s_sound.c
@set GLOBOBJS=%GLOBOBJS% tables.c
@set GLOBOBJS=%GLOBOBJS% v_video.c
@set GLOBOBJS=%GLOBOBJS% wi_stuff.c
@set GLOBOBJS=%GLOBOBJS% w_wad.c
@set GLOBOBJS=%GLOBOBJS% z_zone.c

@rem cl %GLOBOBJS% %CFLAGS% /Fewindows/libdoom.exe
cl %GLOBOBJS% %CFLAGS% /LD /DDllExport=__declspec(dllexport) /Fewindows/libdoom.dll

del *.err
del *.obj
