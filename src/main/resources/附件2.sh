# Don't run multiple instances - start just one screen, named "shairplay":
[[ $(screen -list | grep shairplay) == '' ]] &&
 screen -dmS shairplay sh
# Keep shairplay perpetually running. When it crashes, we can just SIGKILL it, and it comes back:
[[ $(ps aux | grep -v grep | grep pts | grep '/usr/bin/shairplay') == '' ]] &&
 screen -S shairplay -p 0 -X stuff "while true; do /usr/bin/shairplay --apname=Airamaplay --ao_devicename=default; sleep 2s; done"