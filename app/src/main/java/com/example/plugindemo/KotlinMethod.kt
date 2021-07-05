package com.example.plugindemo

/**
 * kotlin方法
 * @author chenjianyu
 * @date 2021/7/5
 */
object KotlinMethod {

    @JvmStatic
    fun execute(): Int{
        method1()
        method2()
        method3()
        return 0
    }

    private fun method1(){
        Thread.sleep(500)
    }

    private fun method2(){
        Thread.sleep(300)

    }

    private fun method3(){
        Thread.sleep(800)
    }

}