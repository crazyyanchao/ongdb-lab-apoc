package data.lab.ongdb.similarity.simhash;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

/**
 * 文本去重算法的simhash类
 * 步骤如下：
 * 1，对文本分词，得到N维特征向量（默认为64维）
 * 2，为分词设置权重（tf-idf）
 * 3，为特征向量计算哈希
 * 4，对所有特征向量加权，累加（目前仅进行非加权累加）
 * 5，对累加结果，大于零置一，小于零置零
 * 6，得到文本指纹（fingerprint）
 *
 * @author Yc-Ma
 * @PACKAGE_NAME: data.lab.ongdb.similarity.simhash.SimHash
 * @Description: TODO
 * @date 2020/5/22 15:10
 */
public class SimHash {

    private String hash;
    private BigInteger signature;
    private KeywordExtractor wordExtractor = KeywordExtractor.getInstance();

    public static SimHash hash() {
        return new SimHash();
    }

    /**
     * @param
     * @return
     * @Description: TODO(检查两个新闻是否相似)
     */
    public static boolean isSimilar(TextFingerPrint textFingerPrintOne, TextFingerPrint textFingerPrintTwo) {
        return isSimilar(textFingerPrintOne, textFingerPrintTwo, 3);
    }

    /**
     * @param
     * @return
     * @Description: TODO(检查两个新闻是否相似)
     */
    public static boolean isSimilar(TextFingerPrint textFingerPrintOne, TextFingerPrint textFingerPrintTwo, int hammingDistance) {

        // 标题指纹
        String titleSimHashOne = textFingerPrintOne.getTitleSimHash();
        String titleSimHashTwo = textFingerPrintTwo.getTitleSimHash();

        // 内容指纹
        String contentSimHashOne = textFingerPrintOne.getContentSimHash();
        String contentSimHashTwo = textFingerPrintTwo.getContentSimHash();

        return (
                (titleSimHashOne != null && !"".equals(titleSimHashOne) &&
                        titleSimHashTwo != null && !"".equals(titleSimHashTwo)
                        && (titleSimHashOne.length() == 64 && titleSimHashTwo.length() == 64) &&
                        (hamming(titleSimHashOne, titleSimHashTwo) < hammingDistance))
                        ||
                        (contentSimHashOne != null && !"".equals(contentSimHashOne) &&
                                contentSimHashTwo != null && !"".equals(contentSimHashTwo) &&
                                (contentSimHashOne.length() == 64 && contentSimHashTwo.length() == 64) &&
                                (hamming(contentSimHashOne, contentSimHashTwo) < hammingDistance))
        );
    }

    private void analysis(String content) {
        Map<String, Double> wordInfos = wordExtractor.extract(content);
        double[] featureVector = new double[FNVHash.HASH_BITS];
        Set<String> words = wordInfos.keySet();
        for (String word : words) {
            BigInteger wordHash = FNVHash.fnv1aHash64(word);
            for (int i = 0; i < FNVHash.HASH_BITS; i++) {
                BigInteger bitmask = BigInteger.ONE.shiftLeft(FNVHash.HASH_BITS - i - 1);
                if (wordHash.and(bitmask).signum() != 0) {
                    featureVector[i] += wordInfos.get(word);
                } else {
                    featureVector[i] -= wordInfos.get(word);
                }
            }
        }

        BigInteger signature = BigInteger.ZERO;
        StringBuffer hashBuffer = new StringBuffer();
        for (int i = 0; i < FNVHash.HASH_BITS; i++) {
            if (featureVector[i] >= 0) {
                signature = signature.add(BigInteger.ONE.shiftLeft(FNVHash.HASH_BITS - i - 1));
                hashBuffer.append("1");
            } else {
                hashBuffer.append("0");
            }
        }
        this.hash = hashBuffer.toString();
        this.signature = signature;
    }

    /**
     * 汉明距离
     *
     * @param targetSignature 比较签名
     * @return
     */
    public int getHammingDistance(BigInteger targetSignature) {
        BigInteger x = this.getSignature().xor(targetSignature);
        int tot = 0;

        // 统计x中二进制位数为1的个数
        // 我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，
        // 对吧，然后，n&(n-1)就相当于把后面的数字清0，
        // 我们看n能做多少次这样的操作就OK了。

        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }

        return tot;
    }

    /**
     * 汉明距离
     *
     * @param targetSignature 比较签名
     * @return
     */
    public static int hammingDistance(BigInteger sourceSignature, BigInteger targetSignature) {
        BigInteger x = sourceSignature.xor(targetSignature);
        int tot = 0;

        // 统计x中二进制位数为1的个数
        // 我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，
        // 对吧，然后，n&(n-1)就相当于把后面的数字清0，
        // 我们看n能做多少次这样的操作就OK了。

        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }

        return tot;
    }

    /**
     * hash距离。二进制比较
     *
     * @param targetHash 比较目标
     * @return
     */
    public int getHashDistance(String targetHash) {
        int distance;
        if (this.getHash().length() != targetHash.length()) {
            distance = -1;
        } else {
            distance = 0;
            for (int i = 0; i < this.getHash().length(); i++) {
                if (this.getHash().charAt(i) != targetHash.charAt(i)) {
                    distance++;
                }
            }
        }
        return distance;
    }

    /**
     * hash距离。二进制比较
     *
     * @param targetHash 比较目标
     * @return
     */
    public static int hashDistance(String sourceHash, String targetHash) {
        int distance;
        if (sourceHash.length() != targetHash.length()) {
            distance = -1;
        } else {
            distance = 0;
            for (int i = 0; i < sourceHash.length(); i++) {
                if (sourceHash.charAt(i) != targetHash.charAt(i)) {
                    distance++;
                }
            }
        }
        return distance;
    }

    public static int hamming(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return 0;
        }
        int dis = 0;
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                dis++;
            }
        }
        return dis;
    }


    public String getHash() {
        return this.hash;
    }

    public BigInteger getSignature() {
        return this.signature;
    }

    public SimHash setText(String text) {
        this.analysis(text);
        return this;
    }

    public String getSimHash() {

        return this.hash;
    }
}
