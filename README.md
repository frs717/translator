# tinkoff-translator
## REST API
### api url:

POST method
```
/api/translator/translate
```

### input json:
```
{
  "sourceLanguage": "String",
  "targetLanguage": "String",
  "text": "String"
}
```
- **sourceLanguage** - language code of source text (Example, 'en')
- **targetLanguage** - language code for translation (Example, 'en')
- **text** - text for translation

### output json:
```
{
  "text": "String"
}
```

- **text** - translated text

## Docker
### build:
```
docker build -t translator . 
```

### run:
```
docker run -p <EXTERNAL_PORT>:<INTERNAL_PORT> -e JAVA_OPTS="-Dtranslator.yandex.api-key=<API_KEY> -Dtranslator.yandex.api-folder-id=<FOLDER_ID>" translator
```
- **<EXTERNAL_PORT>** - external app port
- **<INTERNAL_PORT>** - internal app port
- **<API_KEY>** - yandex translator api key
- **<FOLDER_ID>** - yandex cloud folder id
