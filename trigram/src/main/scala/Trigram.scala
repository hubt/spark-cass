import org.apache.spark._;
import org.apache.spark.SparkContext._;

object Trigram {
  def main(args: Array[String]) {
    val conf = new SparkConf(true).set("spark.cassandra.connection.host", "127.0.0.1");

    import com.datastax.spark.connector._; 
    val sc = new SparkContext("local","My Cluster",conf);
    val ts = sc.cassandraTable("testkeyspace","trigram");

    // load all trigrams
    val trigrams = ts.select("first","second","third").as((_:String, _: String, _:String));
    println("There are " + trigrams.count + "trigrams");

    // join rows where "first" = "second"
    println("Joining in spark" );
    val firstwords = trigrams.keyBy(_._1);
    val secondwords = trigrams.keyBy(_._2);

    val joined = firstwords.join(secondwords);
    joined.foreach(x => println("key: " + x._1 + " value: " + x._2));
    println("There are " + joined.count + " joined results");
  }
}

