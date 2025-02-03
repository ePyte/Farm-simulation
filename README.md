# About the project
In this multithread Java simulation Sheep and Dog objects are in a 12*12 2D Field.
The simulation ends when the first Sheep leaves the field.


# Visual representation
The Field with the 10 Sheeps ('A' - 'J') and 5 Dogs ('1' - '5') are represented by characters and visable in the terminal.
The Sheeps start in the beginning of the field and originally can choose from the 8 adjacent different Fields as a next step.
The Dogs influence the direction of the Sheeps.
The Farm's each side has only one Gate where the Sheeps can leave.
Each entity has its own respective thread.

Starting Field example:

```
|#|#|#|#|#|#|#|#|#|#|#|_|#|#|
|#| | | | | | | | | | | | |#|
|#| | | | | | | | | | | | |_|
|#|4| | | | | | | | | | | |#|
|#| | | | | | | | | | | | |#|
|#| | | |1|I|D| |B| | | | |#|
|#| |3| | |J|A|C| | |2| | |#|
|#| | | | |G| |E| | | | | |#|
|#| | | | | | |F|H| | | | |#|
|_| | | | | | | | | | | | |#|
|#| | | | | | | | | | | | |#|
|#| | | | | | | | | | | | |#|
|#| | | | | | |5| | | | | |#|
|#|#|#|#|#|_|#|#|#|#|#|#|#|#|
```

Game over Field example:
```
|#|#|#|#|#|#|#|#|#|_|#|#|#|#|
|#| | | | | | | |2| | | | |#|
|#| | | | | | | | | | | | |#|
|#| | | | | | | |3| | | | |#|
|#| | | | | | | | | | | | |#|
|#| | | | | |I|E| |1| | | |#|
|#| | | | | |F|C| | |4| |D|_|
|#| | | |G|A| |B| | | | | |#|
|#| | | | |J| | | | | | | |#|
|#| | | | | | | |H| | | | |#|
|#| | | | | | | | | | | | |#|
|_| | | | | | | | | | | | |#|
|#| | | | | | | | | | |5| |#|
|#|#|#|_|#|#|#|#|#|#|#|#|#|#|
Game Over
Sheep D has left the area.
```

# Built with
- Programming language: Java - 17.0.10
- Compiler: gcc 9.4.0
- Opertaing system: Microsoft Windows 10 (10.0.19045)
- Processor: 6 cores, 12 logical procesors



