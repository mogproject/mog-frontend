SBT = sbt
OPEN = open
APP_NAME = mog-frontend
PROD_RSC = docs

build:
	${DEV_CMD}

test:
	${SBT} test

console:
	${SBT} test:console

clean:
	rm -rf ~/.sbt/0.13/staging/*/mog-* && ${SBT} clean

local:
	${OPEN} http://localhost:8000/index-dev.html?dev=true

local_mobile:
	${OPEN} http://localhost:8000/index-dev.html?mobile=true

server:
	python -m 'http.server'

.PHONY: build test console clean local local_mobile server

