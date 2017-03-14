package com.mllib

import org.apache.spark.mllib.classification.LogisticRegressionWithSGD
import org.apache.spark.mllib.evaluation.{BinaryClassificationMetrics, MulticlassMetrics}
import org.apache.spark.mllib.feature.StandardScaler
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

/**
 * Created by lichangyue on 2017/1/18.
 */
object LogisticRegrTest {

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName(this.getClass.getSimpleName).setMaster("local[*]")
    val sc = new SparkContext(conf)

//    "http://www.bloomberg.com/news/2010-12-23/ibm-predicts-holographic-calls-air-breathing-batteries-by-2015.html"
// 	"4042"
// "{""title"":""IBM Sees Holographic Calls Air Breathing Batteries ibm sees holographic calls, air-breathing batteries"",""body"":""A sign stands outside the International Business Machines Corp IBM Almaden Research Center campus in San Jose California Photographer Tony Avelar Bloomberg Buildings stand at the International Business Machines Corp IBM Almaden Research Center campus in the Santa Teresa Hills of San Jose California Photographer Tony Avelar Bloomberg By 2015 your mobile phone will project a 3 D image of anyone who calls and your laptop will be powered by kinetic energy At least that s what International Business Machines Corp sees in its crystal ball The predictions are part of an annual tradition for the Armonk New York based company which surveys its 3 000 researchers to find five ideas expected to take root in the next five years IBM the world s largest provider of computer services looks to Silicon Valley for input gleaning many ideas from its Almaden research center in San Jose California Holographic conversations projected from mobile phones lead this year s list The predictions also include air breathing batteries computer programs that can tell when and where traffic jams will take place environmental information generated by sensors in cars and phones and cities powered by the heat thrown off by computer servers These are all stretch goals and that s good said Paul Saffo managing director of foresight at the investment advisory firm Discern in San Francisco In an era when pessimism is the new black a little dose of technological optimism is not a bad thing For IBM it s not just idle speculation The company is one of the few big corporations investing in long range research projects and it counts on innovation to fuel growth Saffo said Not all of its predictions pan out though IBM was overly optimistic about the spread of speech technology for instance When the ideas do lead to products they can have broad implications for society as well as IBM s bottom line he said Research Spending They have continued to do research when all the other grand research organizations are gone said Saffo who is also a consulting associate professor at Stanford University IBM invested 5 8 billion in research and development last year 6 1 percent of revenue While that s down from about 10 percent in the early 1990s the company spends a bigger share on research than its computing rivals Hewlett Packard Co the top maker of personal computers spent 2 4 percent last year At Almaden scientists work on projects that don t always fit in with IBM s computer business The lab s research includes efforts to develop an electric car battery that runs 500 miles on one charge a filtration system for desalination and a program that shows changes in geographic data IBM rose 9 cents to 146 04 at 11 02 a m in New York Stock Exchange composite trading The stock had gained 11 percent this year before today Citizen Science The list is meant to give a window into the company s innovation engine said Josephine Cheng a vice president at IBM s Almaden lab All this demonstrates a real culture of innovation at IBM and willingness to devote itself to solving some of the world s biggest problems she said Many of the predictions are based on projects that IBM has in the works One of this year s ideas that sensors in cars wallets and personal devices will give scientists better data about the environment is an expansion of the company s citizen science initiative Earlier this year IBM teamed up with the California State Water Resources Control Board and the City of San Jose Environmental Services to help gather information about waterways Researchers from Almaden created an application that lets smartphone users snap photos of streams and creeks and report back on conditions The hope is that these casual observations will help local and state officials who don t have the resources to do the work themselves Traffic Predictors IBM also sees data helping shorten commutes in the next five years Computer programs will use algorithms and real time traffic information to predict which roads will have backups and how to avoid getting stuck Batteries may last 10 times longer in 2015 than today IBM says Rather than using the current lithium ion technology new models could rely on energy dense metals that only need to interact with the air to recharge Some electronic devices might ditch batteries altogether and use something similar to kinetic wristwatches which only need to be shaken to generate a charge The final prediction involves recycling the heat generated by computers and data centers Almost half of the power used by data centers is currently spent keeping the computers cool IBM scientists say it would be better to harness that heat to warm houses and offices In IBM s first list of predictions compiled at the end of 2006 researchers said instantaneous speech translation would become the norm That hasn t happened yet While some programs can quickly translate electronic documents and instant messages and other apps can perform limited speech translation there s nothing widely available that acts like the universal translator in Star Trek Second Life The company also predicted that online immersive environments such as Second Life would become more widespread While immersive video games are as popular as ever Second Life s growth has slowed Internet users are flocking instead to the more 2 D environments of Facebook Inc and Twitter Inc Meanwhile a 2007 prediction that mobile phones will act as a wallet ticket broker concierge bank and shopping assistant is coming true thanks to the explosion of smartphone applications Consumers can pay bills through their banking apps buy movie tickets and get instant feedback on potential purchases all with a few taps on their phones The nice thing about the list is that it provokes thought Saffo said If everything came true they wouldn t be doing their job To contact the reporter on this story Ryan Flinn in San Francisco at rflinn bloomberg net To contact the editor responsible for this story Tom Giles at tgiles5 bloomberg net by 2015, your mobile phone will project a 3-d image of anyone who calls and your laptop will be powered by kinetic energy. at least that\u2019s what international business machines corp. sees in its crystal ball."",""url"":""bloomberg news 2010 12 23 ibm predicts holographic calls air breathing batteries by 2015 html""}"
// 	"business"
// "0.789131"
// "2.055555556"
// "0.676470588"
// "0.205882353"
// "0.047058824"
// "0.023529412"
// "0.443783175"
// "0"
// "0"
// "0.09077381"
// "0"
// "0.245831182"
// "0.003883495"
// "1"
// "1"
// "24"
// "0"
// "5424"
// "170"
// "8"
// "0.152941176"
// "0.079129575"
// "0"


    val rawData = sc.textFile("data/mllib/logic/train.tsv")
    val records = rawData.map(_.split("\t"))

    //2.添加类别特征
    //数据第三列是类别，先计算总类数，然后建立一个类别到序号的map
    val category = records.map(r=>r(3)).distinct.collect().zipWithIndex.toMap

    //与之前不同的是添加了一个向量categoryFeatures用于标识类别


    val data= records.map(point => {

      val replaceData = point.map(_.replaceAll("\"",""))
      //数据的前四个字段用不到，最后一个字段是分类结果，1位长久，0位短暂
      val label = replaceData(replaceData.size -1).toInt

      //添加categoryFeatures用于标识识别
      val categoriesIndex = category(point(3))
      val categoryFeatures = Array.ofDim[Double](category.size) //ofDim创建多维数组
      categoryFeatures(categoriesIndex) = 1.0

      val otherFeatures = replaceData.slice(4,replaceData.size-1)
        .map(x=> if(x=="?") 0.0 else x.toDouble)
      //合并两个数组
      val features = otherFeatures ++ categoryFeatures

      //label存分类结果，features存特征，将其转换为LabeledPoint类型，此类型主要用于监督学习。
      LabeledPoint(label, Vectors.dense(features))
    })

    //1.特征标准化
    val vecotrs = data.map(p=>p.features)
    val scaler = new StandardScaler(withMean = true,withStd = true).fit(vecotrs)
    val scalerData = data.map(point =>
    LabeledPoint(point.label,scaler.transform(point.features))
    )





    //随机梯度下降
    val lrModel= LogisticRegressionWithSGD.train(scalerData,1000)

    //预测并查看有多少预测正确，这里的测试集与训练数据集相同
    val predictureData = data.map{point=>
      if(lrModel.predict(point.features) == point.label) 1 else 0
    }.sum()

    val accuracy = predictureData/data.count()
    println("正确率：" + accuracy)

    val predicture = data.map{point=>
      (lrModel.predict(point.features),point.label)
    }


   /* val metrics = new MulticlassMetrics(predicture)

    val f = metrics.fMeasure(1.0)
    println("f:"+f)

    println("precision:"+metrics.precision(1.0))

    println("precision2:"+metrics.precision)

    println("recall:"+metrics.recall)*/



    val metrics = new BinaryClassificationMetrics(predicture)

    // Precision by threshold
    val precision = metrics.precisionByThreshold
    precision.foreach { case (t, p) =>
      println(s"Threshold: $t, Precision: $p")
    }

    // Recall by threshold
    val recall = metrics.recallByThreshold
    recall.foreach { case (t, r) =>
      println(s"Threshold: $t, Recall: $r")
    }

    // Precision-Recall Curve
    val PRC = metrics.pr

    // F-measure
    val f1Score = metrics.fMeasureByThreshold
    f1Score.foreach { case (t, f) =>
      println(s"Threshold: $t, F-score: $f, Beta = 1")
    }

    val beta = 0.5
    val fScore = metrics.fMeasureByThreshold(beta)
    f1Score.foreach { case (t, f) =>
      println(s"Threshold: $t, F-score: $f, Beta = 0.5")
    }

    // AUPRC
    val auPRC = metrics.areaUnderPR
    println("Area under precision-recall curve = " + auPRC)

    // Compute thresholds used in ROC and PR curves
    val thresholds = precision.map(_._1)

    // ROC Curve
    val roc = metrics.roc

    // AUROC
    val auROC = metrics.areaUnderROC
    println("Area under ROC = " + auROC)



  }

}
