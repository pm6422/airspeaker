#!/bin/bash
#
# This starts and stops shairplay
#
### BEGIN INIT INFO
# Provides: shairplay
# Required-Start: $network
# Required-Stop:
# Short-Description: Free portable AirPlay server implementation similar to ShairPort.
# Description: Free portable AirPlay server implementation similar to ShairPort.
# Default-Start: 2 3 4 5
# Default-Stop: 0 1 6
### END INIT INFO


# Source function library.
. /lib/lsb/init-functions

DAEMON="/usr/local/bin/shairplay"
AIRPORT_KEY_DIR="/usr/local/share/shairplay"

[ -x $binary ] || exit 0

RETVAL=0

start() {
 echo -n "Starting shairplay: "
 start-stop-daemon --start --quiet --chdir $AIRPORT_KEY_DIR \
 --exec "$DAEMON" -b --oknodo -- -a 'ShairPlay Speaker'
 log_end_msg $?
}

stop() {
 echo -n "Shutting down shairplay: "
 start-stop-daemon --stop --quiet --exec "$DAEMON" \
 --retry 1 --oknodo
 log_end_msg $?
}

restart() {
 stop
 sleep 1
 start
}

case "$1" in
 start)
 start
 ;;
 stop)
 stop
 ;;
 status)
 status shairplay
 ;;
 restart)
 restart
 ;;
 *)
 echo "Usage: $0 {start|stop|status|restart}"
 ;;
esac
exit 0