package org.talend.hadoop.distribution.constants;

public enum ModuleGroupName {
    AZURE("AZURE-GROUP"), //$NON-NLS-1$
    BIGQUERY("BIGQUERY-GROUP"), //$NON-NLS-1$
    CASSANDRA("CASSANDRA-GROUP"), //$NON-NLS-1$
    DYNAMODB_BATCH("DYNAMODB-BATCH-GROUP"), //$NON-NLS-1$
    DYNAMODB_STREAMING("DYNAMODB-STREAMING-GROUP"), //$NON-NLS-1$
    DELTALAKE("DELTALAKE-GROUP"), //$NON-NLS-1$
    FLUME("FLUME-GROUP"), //$NON-NLS-1$
    GCS("GCS-GROUP"), //$NON-NLS-1$
    GRAPHFRAMES("GRAPHFRAMES-GROUP"), //$NON-NLS-1$
    HDFS("HDFS-GROUP"), //$NON-NLS-1$
    HBASE("HBASE-GROUP"), //$NON-NLS-1$
    HIVE("HIVE-GROUP"), //$NON-NLS-1$
    IMPALA("IMPALA-GROUP"), //$NON-NLS-1$
    KAFKA("KAFKA-GROUP"), //$NON-NLS-1$
    KINESIS("KINESIS-GROUP"), //$NON-NLS-1$
    KMEANS("KMEANS-GROUP"), //$NON-NLS-1$
    MATH("MATH-GROUP"), //$NON-NLS-1$
    ML("ML-GROUP"), //$NON-NLS-1$
    MODEL_ENCODER("MODEL-ENCODER-GROUP"), //$NON-NLS-1$
    PARQUET("PARQUET-GROUP"), //$NON-NLS-1$
    PUBSUB("PUBSUB-GROUP"), //$NON-NLS-1$
    REDSHIFT_BATCH("REDSHIFT-BATCH-GROUP"), //$NON-NLS-1$
    REDSHIFT_STREAMING("REDSHIFT-STREAMING-GROUP"), //$NON-NLS-1$
    REST("REST-GROUP"), //$NON-NLS-1$
    S3("S3-GROUP"), //$NON-NLS-1$
    SNOWFLAKE("SNOWFLAKE-GROUP"), //$NON-NLS-1$
    SPARK_COMPILE("SPARK-COMPILE-GROUP"), //$NON-NLS-1$
    SPARK_BATCH("SPARK-BATCH-GROUP"), //$NON-NLS-1$
    SPARK_STREAMING("SPARK-STREAMING-GROUP"), //$NON-NLS-1$
    SQOOP("SQOOP-GROUP"), //$NON-NLS-1$
    SQOOP_SEQUENCE("SQOOP-SEQUENCE-GROUP"), //$NON-NLS-1$
    SQOOP_AVRO("SQOOP-AVRO-GROUP"), //$NON-NLS-1$
    SQOOP_TEXT("SQOOP-TEXT-GROUP"), //$NON-NLS-1$
    SQOOP_PARQUET("SQOOP-PARQUET-GROUP"), //$NON-NLS-1$
    TDM("TDM-GROUP"), //$NON-NLS-1$
    WEBHDFS("WEBHDFS-GROUP"); //$NON-NLS-1$

    private String baseName;

    private ModuleGroupName(String baseName) {
        this.baseName = baseName;
    }

    public String get(String distributionVersion) {
        return this.baseName + "-" + distributionVersion; //$NON-NLS-1$
    }
}
