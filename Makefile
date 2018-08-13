SBT = sbt
OPEN = open
PYTHON = python3
APP_NAME = mog-frontend
PROD_RSC = docs
TEST_ASS = assets
PROD_ASS = ${PROD_RSC}/assets
DEV_PORT = 8001
TARGET = target/scala-2.12/${APP_NAME}-test-
COPY_PROD = cp -f ${TARGET}opt.js ${TARGET}opt.js.map  ${PROD_ASS}/js/ && cp -rf ${TEST_ASS}/* ${PROD_ASS}/
REMOVE_MAPPING = sed -i '' -e '/\/\/\# sourceMappingURL.*/d' ${PROD_ASS}/js/${APP_NAME}*-opt.js
UGLIFY_CSS = rm -f ${PROD_ASS}/css/* && uglifycss ${TEST_ASS}/css/[bmps]* > ${PROD_ASS}/css/pg.min.css && uglifycss ${TEST_ASS}/css/notesview.css > ${PROD_ASS}/css/notesview.css


build:
	${SBT} test:fastOptJS

test:
	${SBT} test

console:
	${SBT} test:console

clean:
	rm -rf ~/.sbt/0.13/staging/*/mog-*
	${SBT} clean

local:
	${OPEN} http://localhost:${DEV_PORT}/index-dev-debug.html?debug=true

local_mobile:
	${OPEN} http://localhost:${DEV_PORT}/index-dev-debug.html?device=1

server:
	${PYTHON} -m 'http.server' ${DEV_PORT}

publish: test
	${SBT} test:fullOptJS
	${COPY_PROD}
	${REMOVE_MAPPING}
	${UGLIFY_CSS}

publish-commit: publish
	git add .
	git commit -m Publish
	git push

merge:
	git checkout master
	git pull
	git checkout develop
	git merge master
	git push

bench:
	${SBT} 'set logLevel := Level.Error' 'set scalaJSStage in Global := FullOptStage' 'test:run-main com.mogproject.mogami.frontend.bench.BenchmarkJS'


.PHONY: build test console clean local local_mobile server publish publish-commit merge bench

