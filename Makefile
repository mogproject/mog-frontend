SBT = sbt
OPEN = open
APP_NAME = mog-frontend
PROD_RSC = docs
TEST_ASS = assets
PROD_ASS = ${PROD_RSC}/assets
DEV_PORT = 8001
TARGET = target/scala-2.12/${APP_NAME}-test-
COPY_PROD = cp -f ${TARGET}opt.js ${TARGET}opt.js.map  ${PROD_ASS}/js/ && cp -rf ${TEST_ASS}/* ${PROD_ASS}/

build:
	${SBT} test:fastOptJS

test:
	${SBT} test

console:
	${SBT} test:console

clean:
	rm -rf ~/.sbt/0.13/staging/*/mog-* && ${SBT} clean

local:
	${OPEN} http://localhost:${DEV_PORT}/index-dev-debug.html?debug=true

local_mobile:
	${OPEN} http://localhost:${DEV_PORT}/index-dev-debug.html?device=1

server:
	python -m 'http.server' ${DEV_PORT}

publish: test
	${SBT} test:fullOptJS && ${COPY_PROD}

publish-commit: publish
	git add . && git commit -m Publish && git push

merge:
	git checkout master && git pull && git checkout develop && git merge master && git push

.PHONY: build test console clean local local_mobile server publish publish-commit

