# Plugin overview

I decided to add more factory-things to minecraft:

(Electricity is bound to the player who placed the machine)

- Generators: Produce electricity for burnable materials
- Electric Furnace: Smelts items at the cost of electricity
- Pipes: Allows you to pipe things around from machines / chests into machines/chests
- Quarry: At the cost of electricity the quarry will mine all ores in a chunk, once its done it displays a barrier particle above itself

(The generator and furnace have an inventory which opens on right-click while the quarry will show you where it's currently mining at)

## Pipes

(A video tutorial on pipes can be found [here](https://youtu.be/RcVNO-yUX6g): )

By default, pipes only push items from their internal buffer in the direction that you were looking from when you placed them.
(This means that if you place a pipe while looking down it will always try to push its contents upwards.)
To make a pipe useful you will have to give pipes a diamond which allows them to extract items from inventories
(Pipes only extract from the opposite face they've been placed from)
Thus: You only need one diamond at the start of the pipe, no matter how long it will be

## Recipes

Recipes can be found and modified in server/DevathonPlugin/machines.json