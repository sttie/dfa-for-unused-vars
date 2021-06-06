# Dataflow анализ для C++. Задание 3

Реализовать анализ неиспользуемых присваиваний.

Удачи мне.

UPD: обидненький проигрыш, но вполне справедливый.

## Примеры работы

Пример 1 (из условия)
```
a = 1
b = a
x = 3
y = 4

while (b < 5)
  z = x
  b = b + 1
  x = 9
  y = 10
end
```

Результат:
```
Unused variables:
y = 4
y = 10
z = x
```

Пример 2 (из условия, но с всегда ложным условием цикла)
```
a = 1
b = a
x = 3
y = 4

while 10 < 5
  z = x
  b = b + 1
  x = 9
  y = 10
end
```

Результат:
```
b = a
x = 3
y = 4
```
