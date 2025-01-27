package com.zhisheng.examples.streaming.socket;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * Desc: socket
 * Created by zhisheng on 2019-04-26
 * blog：http://www.54tianzhisheng.cn/
 * 微信公众号：zhisheng
 */
public class Main {
    public static void main(String[] args) throws Exception {
        //参数检查
        if (args.length != 2) {
            System.err.println("USAGE:\nSocketTextStreamWordCount <hostname> <port>");
            return;
        }

        String hostname = args[0];
        Integer port = Integer.parseInt(args[1]);


        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //获取数据
        DataStreamSource<String> stream = env.socketTextStream(hostname, port);

        //计数
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = stream.flatMap(new LineSplitter())
                .keyBy(0)
                .sum(1);

        sum.print();

        env.execute("Java WordCount from SocketTextStream Example");
    }

    /**
     * 泛型参数<String, Tuple2<String, Integer>>表明这个函数接收String类型的输入，输出是Tuple2<String, Integer>类型的元组。
     */
    public static final class LineSplitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) {
            // 输入字符串s首先被转换为小写，然后使用正则表达式\\W+分割。
            // 这个正则表达式匹配任何非单词字符（等同于[^a-zA-Z0-9_]），
            // 所以它根据任何非字母数字的字符串来分割文本，比如空格、标点符号等。
            String[] tokens = s.toLowerCase().split("\\W+");

            for (String token: tokens) {
                // 如果token是一个有效的字符串（长度大于0），这行代码将创建一个新的元组，
                // 其中包含单词和整数1（作为计数），并使用collector将其添加到输出中。
                if (token.length() > 0) {
                    collector.collect(new Tuple2<String, Integer>(token, 1));
                }
            }
        }
    }
}
