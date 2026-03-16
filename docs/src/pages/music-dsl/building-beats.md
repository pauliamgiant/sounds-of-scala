## Building Beats

Here is an example of defining a single drum part in using the Sequence, Rest and DrumStroke classes:


```scala 3
Sequence(
  Rest(Quarter),
  Sequence(
    DrumStroke(Snare, Quarter, Medium),
    Sequence(Rest(Quarter), DrumStroke(Snare, Quarter, Loud)))).repeat(20)
```

This will reproduce a classic snare pattern of playing the snare on beats 2 and 4. 

This nested Sequence however is not the most readable way to define this pattern.

There is a more idiomatic way to write & combine notes courtesy of the syntax provided by the `sounds-of-scala` library.

### + operator
We can use the **`+`** operator to make a more idiomatic definition:

```scala 3
Rest(Quarter) + DrumStroke(Snare, Quarter, Medium) + Rest(Quarter) + DrumStroke(Snare, Quarter, Loud)
```

### Drumstroke builder methods
We also have some DrumStroke builder methods to make this more readable:

```scala 3
RestQuarter + SnareDrum + RestQuarter + SnareDrum
```

And potentially even more concisely:
```scala 3
r4 + sn + r4 + sn
```
These are more readable and are equivalent to the initial definition. 

### * operator
We can also use the **`*`** operator to repeat a pattern:

```scala 3
(RestQuarter + SnareDrum + RestQuarter + SnareDrum) * 20
```

### | operator
If we want to indicate bars, we can use the **`|`** operator:

```scala 3
  RestQuarter + SnareDrum + RestQuarter + SnareDrum 
  | RestQuarter + SnareDrum + RestQuarter + SnareDrum
```

### The 'repeat' method
We can also repeat a section of music a specific number of times using the **`repeat`** method:

```scala 3
(RestQuarter + SnareDrum).repeat(4)
```

For continuous looping during playback, set `Playback.Loop` on the Track instead — see [The Song Type](../music-dsl/songs.md#the-track-type).

### [Next Step: Building Notes](../music-dsl/building-notes.md)






