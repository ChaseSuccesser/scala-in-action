package com.ligx.util

import scala.reflect.runtime.universe._

/**
  * Author: ligongxing.
  * Date: 2017年09月14日.
  */
object ReflectUtil {

  /**
    * 反射执行方法
    *
    * @param t          类
    * @param methodName 方法名
    * @param methodArgs 方法参数
    * @param classArgs  类参数
    * @tparam T
    * @return
    */
  def invokeMethod[T: TypeTag](t: T, methodName: String, methodArgs: Seq[Any], classArgs: Seq[Any]): Any = {
    val iMirror = getInstanceMirror(t, classArgs)._2

    val methodSymbol = typeOf[T].decl(TermName(methodName)).asMethod
    val mMirror = iMirror.reflectMethod(methodSymbol)

    mMirror.apply(methodArgs: _*)
  }

  /**
    * 反射获取某个字段的值
    *
    * @param t         类
    * @param fieldName 字段名
    * @param classArgs 类参数
    * @tparam T
    * @return
    */
  def getField[T: TypeTag](t: T, fieldName: String, classArgs: Seq[Any]): Any = {
    val iMirror = getInstanceMirror(t, classArgs)._2

    val termSymbol = typeOf[T].decl(TermName(fieldName)).asTerm.accessed.asTerm
    val fMirror = iMirror.reflectField(termSymbol)

    fMirror.get
  }

  /**
    * 反射设置字段的值
    *
    * @param t         类
    * @param fieldName 字段名
    * @param value     字段值
    * @param classArgs 类参数
    * @tparam T
    */
  def setField[T: TypeTag](t: T, fieldName: String, value: Any, classArgs: Seq[Any]) = {
    val iMirror = getInstanceMirror(t, classArgs)._2

    val fieldSymbol = typeOf[T].decl(TermName(fieldName)).asTerm.accessed.asTerm
    val fMirror = iMirror.reflectField(fieldSymbol)

    fMirror.set(value)
  }

  /**
    * 构造类实例，并返回InstanceMirror
    *
    * @param t         类
    * @param classArgs 类参数
    * @tparam T
    * @return
    */
  private def getInstanceMirror[T: TypeTag](t: T, classArgs: Seq[Any]): (Any, InstanceMirror) = {
    val rMirror = runtimeMirror(t.getClass.getClassLoader)

    val clazzSymbol = typeOf[T].typeSymbol.asClass
    val clazzMirror = rMirror.reflectClass(clazzSymbol)
    val consSymbol = typeOf[T].decl(termNames.CONSTRUCTOR).asMethod
    val consMirror = clazzMirror.reflectConstructor(consSymbol)
    val instance = consMirror.apply(classArgs)

    (instance, rMirror.reflect(instance))
  }
}
