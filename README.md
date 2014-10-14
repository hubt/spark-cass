

This can build a docker image which has:
```
ubuntu:latest(currently 14.04)
oracle jdk7
spark 1.1.0
python2.7
scala 2.10.4(ubuntu's default 2.9 didn't work)
spark-cassandra-connector 1.1.0
cassandra 2.0.10 (2.1 didn't work)
cassandra java driver 2.1.0(2.1.1 didn't work)

It also does the following:
Loads test data into the cassandra testkeyspace.trigram table(see /root/setup.sql)
Starts cassandra on container startup
```

# Downloading the image
You can download the image `docker pull hubt/spark-cass`


# Using the interactive shell:
For simple interactive use, there's a shell script /root/spark-cass which just calls spark-shell with the extra jar files for cassandra added
```
host# docker run -t -i spark-cass-image
root@docker# ./spark-cass
... # lots of spark startup messages
scala>   import com.datastax.spark.connector._;
# scala tab-completion can't complete sc.cassandraTable() so you have to type it in directly.
scala>   val table = sc.cassandraTable("testkeyspace","trigram");
scala>   table.count

# or us SchemaRDD
scala>   import org.apache.spark.sql.cassandra.CassandraSQLContext;
scala>   val cc = new CassandraSQLContext(sc)
scala>   val rdd: SchemaRDD = cc.sql("SELECT * from testkeyspace.trigram limit 10")
scala>   a.foreach(println)
```

# Using the Trigram sbt project:
This project assumes the test trigram data in /root/setup.sql is loaded into the cassandra db.

The /root/trigram/src/main/scala/Trigram.scala program loads the cassandra table into memory and self joins rows where the first column = the second column.

to build:
```
cd /root/trigram
sbt assembly
```
to run:
```
spark-submit --class Trigram /root/trigram/target/scala-2.10/trigram-spark-cass-assembly-1.0.jar
```

# Building the docker image

If you want to rebuild the docker image, a simple `docker build -t spark-cass-image.` should work. 

More info:
The docker command starts up a cassandra service, Spark uses a local cluster connected to a local cassandra cluster via 127.0.0.1

There is a /root/spark-cass-env.sh script which a user can source to set up CLASSPATH environment variables, instead of using spark-cass to list the jar files for spark-shell. Whether that's better is debatable. 

## Potential improvements:

I'm no expert in any of these components, so improvements can be made. I built this so I could 
jump into testing, not as an example of best practices.

I could build separate cassandra and spark containers.

`sbt assembly` builds an uber jar with all dependencies, this is slow but reduces dependency issues.




