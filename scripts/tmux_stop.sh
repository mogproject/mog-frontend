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

# create panes if they don't exist
tmux select-pane -t ${APP_NAME}.2 && {
    tmux send-keys -t ${APP_NAME}.1 'C-c'
    tmux send-keys -t ${APP_NAME}.0 'C-c'
}

