#!/bin/bash

set -e

# check prerequisites
which tmux >/dev/null 2>&1

# path settings
function __readlink_f {
    local t="$1"; local n=
    (while [ "$t" ]; do cd $(dirname "$t"); n=$(basename "$t"); t=$(readlink "$n"); done; echo "$(pwd -P)/$n")
}

SCRIPT_PATH=$(__readlink_f "$0")
SCRIPT_DIR=$(dirname "${SCRIPT_PATH}")

APP_ROOT=$(dirname ${SCRIPT_DIR})
APP_NAME=$(basename ${APP_ROOT})

# create a new window if one doesn't exist
tmux select-window -t ${APP_NAME} >/dev/null 2>&1 || tmux new-window -n ${APP_NAME} -c ${APP_ROOT}

# create panes if they don't exist
tmux select-pane -t ${APP_NAME}.2 >/dev/null 2>&1 || {
    tmux split-window -h -t ${APP_NAME} -c ${APP_ROOT} 'sbt' 
    tmux swap-pane -D -t ${APP_NAME}.0
    tmux send-keys -t ${APP_NAME}.0 '~test:fastOptJS' 'C-m'

    tmux split-window -v -t ${APP_NAME}.0 -c ${APP_ROOT} 'make server'
    tmux select-pane -t ${APP_NAME}.2
}

