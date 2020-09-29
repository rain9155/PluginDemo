package com.example.plugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;


/**
 * 进行Transform注册的Plugin
 * @author chenjianyu
 * @date 2020/7/16
 */
public class TimeCostPlugin implements Plugin<Project> {

    //当函数运行时间大于threshold阀值时判定为耗时函数，单位ms
    public static long sThreshold = 100L;
    //当package有值时，只打印package包内的耗时函数
    public static String sPackage = "";

    @Override
    public void apply(Project project) {
        try {
            //通过project实例注册一个名为time的扩展
            Time time = project.getExtensions().create("time", Time.class);
            //在project构建完成后获取time扩展中的赋值情况
            project.afterEvaluate(project1 -> {
                if(time.getThreshold() >= 0){
                    sThreshold = time.getThreshold();
                }
                if(time.getAppPackage().length() > 0){
                    sPackage = time.getAppPackage();
                }
            });
            //通过project实例获取android gradle plugin中的名为android的扩展实例
            AppExtension appExtension = (AppExtension) project.getExtensions().getByName("android");
            //调用android的扩展实例即appExtension的registerTransform方法往android gradle plugin中注册我们自定义的Transform
            appExtension.registerTransform(new TimeCostTransform());
        }catch (UnknownDomainObjectException e){
            e.printStackTrace();
        }
    }

    /**
     * 扩展对应的bean类
     */
    static class Time{

        private long mThreshold = -1;
        private String mPackage = "";

        public Time(){}

        public long getThreshold() {
            return mThreshold;
        }

        public void setThreshold(long threshold) {
            this.mThreshold = threshold;
        }

        public String getAppPackage() {
            return mPackage;
        }

        public void setAppPackage(String p) {
            this.mPackage = p;
        }
    }
}
