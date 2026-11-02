# make class files that can be read
msgfmt --verbose --java2 --source -r openehrArchie -l nl -d ./src/main/java ./po/i18n_nl.po
msgfmt --verbose --java2 --source -r openehrArchie -l en -d ./src/main/java ./po/i18n_en.po
