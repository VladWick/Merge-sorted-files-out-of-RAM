<h1>Merge sorted files out of RAM</h1>

Программа осуществляет слияние нескольких входных файлов в один отсортированный. 

Реализован эффективный алгоритм с точки зрения памяти. Сколько угодно большой входной файл не хранится в оперативной памяти.

<hr>

<h3>Как запускать программу (exec-maven-plugin): </h3>

* С помощью "exec-maven-plugin". Последовательно запустить команды:

```bash
mvn install
mvn exec:java "-Dexec.args=-s -d out.txt st1.txt st2.txt st3.txt"
```
* Запустить com.vladwick.App.java в IDE с передачей аргументов:

<hr>
<h3>Как пользоваться</h3>

* Скопировать нужные для слияния файлы внутрь папки src/main/resources
* Указать их имя при запуске программы после '-Dexec.args' в кавычках:
* Примеры команд, которые можно запускать
```bash
mvn exec:java -Dexec.args="-i -d out.txt largeNumber1.txt largeNumber2.txt largeNumber3.txt"
mvn exec:java -Dexec.args="-s -a out.txt largeString1.txt largeString2.txt largeString3.txt"
```

<hr>

<h3>Описание: </h3>
Файл превышает объем RAM - типичная ситуация для БД. А это значит, что сохранять каждую строчку файла в массив и использовать стандартные методы сортировки - невозможно.
Необходимо использовать сортировку слиянием без сохранения информации из файла в оперативную память.

В этой программе предполагается, что входные файлы уже отсортированы, т.е. реализована только вторая часть алгоритма: слияние нескольких файлов в один отсортированный.

<hr>

<h3>Как работает алгоритм: </h3>

Входные файлы, не помещающиеся в оперативную память, разбиваются на отсортированные блоки (temp files), 
строчки которых помещаются и удаляются в TreeMap по мере заполнения выходного файла (out.txt).
Внутри TreeMap реализовано чёрно-красное дерево, поэтому при добавлении элемент автоматически "сортируется", "встаёт на своё место". Детали в комментариях в файле Util.java.
Добавление элементов внутрь TreeMap 



