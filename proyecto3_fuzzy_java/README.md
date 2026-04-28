# Proyecto 3 - Inferencia Difusa en Java

Este proyecto implementa un sistema de toma de decisiones basado en lógica difusa con inferencia Mamdani.

## Compilar

```bash
javac -d out src/*.java
```

## Ejecutar caso por defecto

```bash
java -cp out Main
```

## Ejecutar con entradas propias

```bash
java -cp out Main data/variables.txt data/rules.txt temperatura=115 presion=70
java -cp out Main data/variables_restaurante.txt data/rules_restaurante.txt calidad_servicio=8 calidad_comida=7

#Este es el comando ejecutable en la linea de comandos cuando quieres especificar el tipo de problema a desarrollar en 
#el motor de inferencia caso, Robot. 
java -cp out Main data/variables_robot.txt data/rules_robot.txt velocidad=2.1 distancia_sensor=15

```

## Archivos de configuración

- `data/variables.txt`: define variables lingüísticas y conjuntos triangulares/trapezoidales.
- `data/rules.txt`: define reglas en lenguaje simple tipo `IF ... THEN ...`.

## Formato de variables

```text
VARIABLE;nombre;tipo;min;max
SET;nombreConjunto;TRIANGULAR;a;b;c
SET;nombreConjunto;TRAPEZOIDAL;a;b;c;d
```

## Formato de reglas

```text
IF temperatura IS Fresco AND presion IS Baja THEN accion IS PM
```

También se acepta `OR` como operador entre los dos antecedentes.
