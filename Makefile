SBT = sbt
OPEN = open
APP_NAME = mog-frontend
PROD_RSC = docs
TEST_ASS = assets
PROD_ASS = ${PROD_RSC}/assets
COPY_PROD = cp -f target/scala-2.12/mog-frontend-test-opt.js ${PROD_ASS}/js/ && cp -rf ${TEST_ASS}/* ${PROD_ASS}/

build:
	${DEV_CMD}

test:
	${SBT} test

console:
	${SBT} test:console

clean:
	rm -rf ~/.sbt/0.13/staging/*/mog-* && ${SBT} clean

local:
	${OPEN} http://localhost:8000/index-dev.html?debug=true

local_mobile:
	${OPEN} http://localhost:8000/index-dev.html?device=1

server:
	python -m 'http.server'

publish: test
	sbt test:fullOptJS && ${COPY_PROD}

.PHONY: build test console clean local local_mobile server publish

