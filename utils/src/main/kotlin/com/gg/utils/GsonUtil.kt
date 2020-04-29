package com.gg.utils

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.Reader
import java.lang.reflect.Type


/**
 *  Creator : GG
 *  Time    : 2017/10/25
 *  Mail    : gg.jin.yu@gmail.com
 *  Explain :
 */
object GsonUtil {

    private var gson: Gson? = Gson()


    /**
     * 转成json
     */
    fun jsonToString(`object`: Any): String? {
        var gsonString: String? = null
        if (gson != null) {
            gsonString = gson!!.toJson(`object`)
        }
        return gsonString
    }

    /**
     * 转成bean
     */
    fun <T> jsonToBean(gsonString: String, cls: Class<T>): T? {
        var t: T? = null
        if (gson != null) {
            t = gson!!.fromJson(gsonString, cls)
        }
        return t
    }

//    /**
//     * 转成list 泛型在编译期类型被擦除导致报错
//     */
//    public static <T> List<T> GsonToList(String gsonString, Class<T> cls) {
//        List<T> list = null;
//        if (gson != null) {
//            list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
//            }.getType());
//        }
//        return list;
//    }

    /**
     * 转成list 解决泛型问题
     */
    fun <T> jsonToList(json: String?, cls: Class<T>): ArrayList<T> {
        val list = ArrayList<T>()
        if (TextUtils.isEmpty(json))
            return list
        if (gson != null) {
            val array = JsonParser().parse(json).asJsonArray
            for (elem in array) {
                list.add(gson!!.fromJson(elem, cls))
            }
        }
        return list
    }


    /**
     * 转成list中有map的
     */
    fun <T> jsonToListMaps(gsonString: String): List<Map<String, T>>? {
        var list: List<Map<String, T>>? = null
        if (gson != null) {
            list = gson!!.fromJson<List<Map<String, T>>>(gsonString,
                    object : TypeToken<List<Map<String, T>>>() {

                    }.type)
        }
        return list
    }

    /**
     * 转成map的
     */
    fun <T> jsonToMaps(gsonString: String): Map<String, T>? {
        var map: Map<String, T>? = null
        if (gson != null) {
            map = gson!!.fromJson<Map<String, T>>(gsonString, object : TypeToken<Map<String, T>>() {

            }.type)
        }
        return map
    }


    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(json: String?, type: Class<T>): T {
        return gson!!.fromJson(json, type)
    }

    fun <T> fromJson(json: String?, type: Type): T {
        return gson!!.fromJson(json, type)
    }

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(reader: JsonReader, typeOfT: Type): T {
        return gson!!.fromJson(reader, typeOfT)
    }

    @Throws(JsonSyntaxException::class, JsonIOException::class)
    fun <T> fromJson(json: Reader, classOfT: Class<T>): T {
        return gson!!.fromJson(json, classOfT)
    }

    @Throws(JsonIOException::class, JsonSyntaxException::class)
    fun <T> fromJson(json: Reader, typeOfT: Type): T {
        return gson!!.fromJson(json, typeOfT)
    }

    fun toJson(src: Any): String {
        return gson!!.toJson(src)
    }

    fun toJson(src: Any, typeOfSrc: Type): String {
        return gson!!.toJson(src, typeOfSrc)
    }
}