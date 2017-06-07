APP_NAME = SoMA.soma
APK_OUTPUT_DIR = app/build/outputs/apk
OUTPUT_APK_RELEASE = $(APK_OUTPUT_DIR)/app-release-unsigned.apk
OUTPUT_APK_DEBUG = $(APK_OUTPUT_DIR)/app-debug.apk
DEVICE_APP_PATH = /data/local/tmp/$(APP_NAME)
DB_NAME = locationDatabase
DEVICE_DB_PATH = /data/user/0/$(APP_NAME)/databases/$(DB_NAME)
PUBLIC_APK_PATH = ../dist/public/apk
REMOTE_PUBLIC_PATH = soma/dl/public

define GetVersion
$(shell grep versionCode app/build.gradle | cut -d' ' -f10 | sed 's/ //g')
endef

define getTimestamp
$(shell date +%Y%m%d)
endef

VERSION := $(call GetVersion)
TIMESTAMP := $(call getTimestamp)
REMOTE_PUBLIC_FILE = soma-v$(VERSION)-$(TIMESTAMP).apk

debug:
	./gradlew assembleDebug

release:
	./gradlew assembleRelease

rmdb:
	adb shell rm $(DEVICE_DB_PATH)
	rm ./$(DB_NAME)

pulldb:
	adb pull $(DEVICE_DB_PATH)

db: pulldb
	sqlite3 $(DB_NAME)

install: debug
	adb install -r $(OUTPUT_APK_DEBUG)

dist: debug
	test -f $(OUTPUT_APK_DEBUG) && cp $(OUTPUT_APK_DEBUG) $(PUBLIC_APK_PATH)

dist-release: release
	test -f $(OUTPUT_APK_RELEASE) && cp $(OUTPUT_APK_RELEASE) $(PUBLIC_APK_PATH)

push: debug
	adb push $(OUTPUT_APK_DEBUG) $(DEVICE_APP_PATH)

install-pm: push
	adb shell pm install -r $(DEVICE_APP_PATH)

uninstall:
	adb uninstall $(APP_NAME)

start:
	adb shell am start -n "$(APP_NAME)/$(APP_NAME).MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER

deploy:
	rsync -avz $(OUTPUT_APK_DEBUG) asdf@141.26.69.119:$(REMOTE_PUBLIC_PATH)/$(REMOTE_PUBLIC_FILE)

remote-link-latest:
	ssh asdf@141.26.69.119 ln -sf $(REMOTE_PUBLIC_PATH)/$(REMOTE_PUBLIC_FILE) $(REMOTE_PUBLIC_PATH)/soma-latest.apk

run-emulator:
	/opt/android-sdk/tools/emulator -netdelay none -netspeed full -avd Nexus_5_API_23

