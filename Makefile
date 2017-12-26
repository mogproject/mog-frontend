SBT = sbt
OPEN = open
APP_NAME = mog-frontend
PROD_RSC = docs
TEST_ASS = assets
PROD_ASS = ${PROD_RSC}/assets
DEV_PORT = 8001
COPY_PROD = cp -f target/scala-2.12/mog-frontend-test-opt.js ${PROD_ASS}/js/ && cp -rf ${TEST_ASS}/* ${PROD_ASS}/

build:
	${SBT} fastOptJS

test:
	${SBT} test

console:
	${SBT} test:console

clean:
	rm -rf ~/.sbt/0.13/staging/*/mog-* && ${SBT} clean

local:
	${OPEN} http://localhost:${DEV_PORT}/index-dev.html?debug=true

local_mobile:
	${OPEN} http://localhost:${DEV_PORT}/index-dev.html?device=1

server:
	python -m 'http.server' ${DEV_PORT}

publish: test
	sbt test:fullOptJS && ${COPY_PROD}

.PHONY: build test console clean local local_mobile server publish

