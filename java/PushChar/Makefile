SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell find src -type f \! -name .\* \! -name \*.java | sed s/^src\\///)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | grep -v \\.java$$ | sort | uniq | tr / .)

all: clean bin doc pushchar.jar pushchar.zip PushChar.app pushchar.app.zip

eclipseall: eclipseclean pushchar.jar pushchar.zip PushChar.app pushchar.app.zip

clean:
	rm -rf bin
	rm -rf doc
	rm -rf *.jar
	rm -rf *.zip
	rm -rf *.app
	rm -rf *.dmg

eclipseclean:
	rm -rf *.jar
	rm -rf *.zip
	rm -rf *.app
	rm -rf *.dmg

jni-bin: jni-src
	rm -rf jni-bin
	mkdir -p jni-bin
	rm -f com_kreative_acc_pushchar_MacCharInFont.o
	cc -c -I/Library/Java/Home/include jni-src/com_kreative_acc_pushchar_MacCharInFont.c
	cc -dynamiclib -o jni-bin/libMacCharInFont.jnilib com_kreative_acc_pushchar_MacCharInFont.o -framework JavaVM -framework CoreFoundation -framework ApplicationServices
	rm -f com_kreative_acc_pushchar_MacCharInFont.o

bin: src
	mkdir -p bin
ifeq ($(origin JAVACFLAGS), undefined)
	$(eval JAVACFLAGS = $(shell dep/minJavaVerOpts))
endif
	javac $(JAVACFLAGS) -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc: src
	mkdir -p doc
	javadoc $(JAVADOCFLAGS) -sourcepath src $(PACKAGES) -d doc

pushchar.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF pushchar.jar     \
		-C bin com/kreative/acc/pushchar     \
		-C bin com/kreative/pushchar/main    \
		-C bin com/kreative/pushchar/ttflib  \
		-C bin com/kreative/pushchar/unilib
	chmod +x pushchar.jar

pushchar.zip: dep pushchar.jar
	rm -rf PushChar
	mkdir -p PushChar
	cp -f dep/PushChar.exe PushChar
	cp -f pushchar.jar PushChar
	zip -r pushchar.zip PushChar
	rm -rf PushChar

PushChar.app: PushChar-Pre10.15.app PushChar-MacOS10.15.app PushChar-MacOS11.0.app

PushChar-Pre10.15.app: dep pushchar.jar
	mkdir -p PushChar-Pre10.15.app/Contents/MacOS
	mkdir -p PushChar-Pre10.15.app/Contents/Resources/Java
	cp -f dep/PkgInfo PushChar-Pre10.15.app/Contents
	cp -f dep/Info.plist PushChar-Pre10.15.app/Contents
	cp -f dep/universalJavaApplicationStub-Pre10.15 PushChar-Pre10.15.app/Contents/MacOS/PushChar
	cp -f dep/PushChar.icns PushChar-Pre10.15.app/Contents/Resources
	cp -f pushchar.jar PushChar-Pre10.15.app/Contents/Resources/Java

PushChar-MacOS10.15.app: dep pushchar.jar
	mkdir -p PushChar-MacOS10.15.app/Contents/MacOS
	mkdir -p PushChar-MacOS10.15.app/Contents/Resources/Java
	cp -f dep/PkgInfo PushChar-MacOS10.15.app/Contents
	cp -f dep/Info.plist PushChar-MacOS10.15.app/Contents
	cp -f dep/universalJavaApplicationStub-MacOS10.15 PushChar-MacOS10.15.app/Contents/MacOS/PushChar
	cp -f dep/PushChar.icns PushChar-MacOS10.15.app/Contents/Resources
	cp -f pushchar.jar PushChar-MacOS10.15.app/Contents/Resources/Java

PushChar-MacOS11.0.app: dep pushchar.jar
	mkdir -p PushChar-MacOS11.0.app/Contents/MacOS
	mkdir -p PushChar-MacOS11.0.app/Contents/Resources/Java
	cp -f dep/PkgInfo PushChar-MacOS11.0.app/Contents
	cp -f dep/Info.plist PushChar-MacOS11.0.app/Contents
	cp -f dep/universalJavaApplicationStub-MacOS11.0 PushChar-MacOS11.0.app/Contents/MacOS/PushChar
	cp -f dep/PushChar.icns PushChar-MacOS11.0.app/Contents/Resources
	cp -f pushchar.jar PushChar-MacOS11.0.app/Contents/Resources/Java

pushchar.app.zip: PushChar.app
	zip -r pushchar.app.zip PushChar*.app

pushchar.dmg: PushChar.app
	rm -rf dmgtmp
	mkdir -p dmgtmp
	cp -R PushChar*.app dmgtmp
	hdiutil create -fs HFS+ -volname "PushChar" -srcfolder dmgtmp pushchar.dmg
	rm -rf dmgtmp

install: pushchar.jar
	sudo mkdir -p /usr/local/lib/
	sudo cp -f pushchar.jar /usr/local/lib/pushchar.jar
	sudo chmod -f 644 /usr/local/lib/pushchar.jar
	sudo mkdir -p /usr/local/bin/
	sudo cp -f dep/pushchar /usr/local/bin/pushchar
	sudo chmod -f 755 /usr/local/bin/pushchar
	sudo mkdir -p /usr/share/icons/hicolor/128x128/apps/
	sudo cp -f dep/pushchar.png /usr/share/icons/hicolor/128x128/apps/pushchar.png
	sudo chmod -f 644 /usr/share/icons/hicolor/128x128/apps/pushchar.png
	sudo mkdir -p /usr/share/applications/
	sudo cp -f dep/pushchar.desktop /usr/share/applications/pushchar.desktop
	sudo chmod -f 644 /usr/share/applications/pushchar.desktop

.PHONY: all eclipseall clean eclipseclean install
