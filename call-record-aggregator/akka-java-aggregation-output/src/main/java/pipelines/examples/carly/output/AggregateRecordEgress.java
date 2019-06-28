package pipelines.examples.carly.output;

import akka.NotUsed;
import akka.stream.javadsl.*;
import pipelines.streamlets.*;
import pipelines.streamlets.avro.*;
import pipelines.akkastream.*;
import pipelines.akkastream.javadsl.*;

import pipelines.examples.carly.data.*;


public class AggregateRecordEgress extends AkkaStreamlet {
  public AvroInlet<AggregatedCallStats> in = AvroInlet.create("in", AggregatedCallStats.class);

  private Object doPrint(final AggregatedCallStats metric) {
    System.out.println(metric);
    return metric;
  }

  @Override public StreamletShape shape() {
    return StreamletShape.createWithInlets(in);
  }

  @Override
  public StreamletLogic createLogic() {
    return new RunnableGraphStreamletLogic(getStreamletContext()) {
      @Override
      public RunnableGraph<?> createRunnableGraph() {
        return getAtLeastOnceSource(in)
          .via(flowWithContext().asFlow())
          .to(getAtLeastOnceSink());
      }
    };
  }

  private FlowWithContext<AggregatedCallStats,PipelinesContext,Object,PipelinesContext,NotUsed> flowWithContext() {
    return FlowWithPipelinesContext.<AggregatedCallStats>create().map(metric -> doPrint(metric));
  }
}
