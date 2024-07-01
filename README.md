# GpxAnalyzer
Десктоп приложение для обработки треков в формате GPX
# Запуск
Для запуска приложения необходимо подключить к проекту [SDK](https://www.oracle.com/java/technologies/install-javafx-sdk.html).
После этого необходимо собрать проект с помощью IDE или вводом команды 
> mvn clean install

Для запуска также необходимо выставить настройки с указанием пути до SDK VM options
> --module-path "path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml,javafx.web --add-exports javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED 