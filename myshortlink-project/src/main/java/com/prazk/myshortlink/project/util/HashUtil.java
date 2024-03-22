package com.prazk.myshortlink.project.util;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

import static com.prazk.myshortlink.project.common.constant.LinkConstant.LINK_LENGTH;

public class HashUtil {
    private static final char[] BASE62 = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd',
            'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z'
    };

    /**
     * 将长链接转换为 Base62的 6位短链接
     */
    public static String linkToBase62(String link) {
        return linkToBase62(link, LINK_LENGTH);
    }

    /**
     * 将长链接转换为 Base62的 length位短链接
     */
    public static String linkToBase62(String link, int length) {
        // 通过 32位 murmurhash 算法将长链接散列为整型
        HashFunction hashFunction = Hashing.murmur3_32_fixed();
        HashCode hashCode = hashFunction.hashString(link, StandardCharsets.UTF_8);
        long number = hashCode.padToLong();

        // 将整型转换为 length位 base62
        StringBuilder sb = new StringBuilder();
        do {
            sb.insert(0, BASE62[(int)(number % BASE62.length)]);
            number /= BASE62.length;
        } while (number > 0);

        // 补齐 length位短链接
        int pad = length - sb.length();
        StringBuilder prefix = new StringBuilder();
        while (pad-- > 0) {
            prefix.append("0");
        }

        return prefix.append(sb).toString();
    }

}
