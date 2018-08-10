# demux-java

Using Java impl [demux-js](https://github.com/EOSIO/demux-js)

Demux is a backend infrastructure pattern for sourcing blockchain events to deterministically update queryable datastores and trigger side effects.

## Example

### Run example
```
mvn clean package
cd example/target
java -jar demux-example-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### EOS transfer demux
``` java
NodeosActionReader actionReader = new NodeosActionReader("http://api.bp.antpool.com", 0);
TransferUpdater updater = new TransferUpdater("eosio.token::transfer");
TransferEffects effect = new TransferEffects("eosio.token::transfer");
TransferActionHandler actionHandler = new TransferActionHandler(Lists.newArrayList(updater), Lists.newArrayList(effect), new TransferState());
BaseActionWatcher watcher = new BaseActionWatcher(actionReader, actionHandler, 500);
watcher.watch();
```

## Features in the work
 - using [disruptor](https://github.com/LMAX-Exchange/disruptor) handling blockchain events