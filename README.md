# StringsSorter

## Общая информация

* **Версия Java:** >=21
* **Версия Maven:** >=3.6.6
* **Зависимости** (уже указаны в pom.xml):

```
<dependencies>
    <dependency>
        <groupId>commons-cli</groupId>
        <artifactId>commons-cli</artifactId>
        <version>1.9.0</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>central</id>
        <url>https://repo.maven.apache.org/maven2</url>
    </repository>
</repositories>
```

## Инструкция к запуску

1. Клонирование репозитория
```
git clone git@github.com:mrzlkvvv/StringsSorter.git
```

2. Установка Java и Maven
```
sudo pacman -S jdk-openjdk maven
```

3. Установка зависимостей и сборка
```
mvn install && mvn compile
```

4. Запуск
```
mvn exec:java -q \
-Dexec.mainClass="ru.outofmemory.Main" \
-Dexec.args="-s -a -p sample- in1.txt in2.txt"  # здесь Вы можете указать свои параметры
```
