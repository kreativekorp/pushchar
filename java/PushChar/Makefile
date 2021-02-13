SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell find src -type f \! -name .\* \! -name \*.java | sed s/^src\\///)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | sort | uniq | tr / .)

all: clean bin doc pushchar-legacy.jar pushchar.jar

eclipseall: eclipseclean pushchar-legacy.jar pushchar.jar

clean:
	rm -rf bin
	rm -rf doc
	rm -rf *.jar

eclipseclean:
	rm -rf *.jar

jni-bin: jni-src
	rm -rf jni-bin
	mkdir -p jni-bin
	rm -f com_kreative_acc_pushchar_MacCharInFont.o
	cc -c -I/Library/Java/Home/include jni-src/com_kreative_acc_pushchar_MacCharInFont.c
	cc -dynamiclib -o jni-bin/libMacCharInFont.jnilib com_kreative_acc_pushchar_MacCharInFont.o -framework JavaVM -framework CoreFoundation -framework ApplicationServices
	rm -f com_kreative_acc_pushchar_MacCharInFont.o

bin: src
	mkdir -p bin
	javac -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc: src
	mkdir -p doc
	javadoc -sourcepath src $(PACKAGES) -d doc

pushchar-legacy.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST-LEGACY.MF pushchar-legacy.jar  \
		-C bin com/kreative/acc/pushchar                \
		-C bin com/kreative/pushchar/legacy
	chmod +x pushchar-legacy.jar

pushchar.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF pushchar.jar     \
		-C bin com/kreative/acc/pushchar     \
		-C bin com/kreative/pushchar/main    \
		-C bin com/kreative/pushchar/ttflib  \
		-C bin com/kreative/pushchar/unilib
	chmod +x pushchar.jar

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
