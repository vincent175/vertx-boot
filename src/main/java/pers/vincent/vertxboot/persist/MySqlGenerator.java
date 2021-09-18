package pers.vincent.vertxboot.persist;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Scanner;

/**
 * @author: Vincent
 * @date: 2021/9/16
 */
public class MySqlGenerator {
    private static final String url = "jdbc:mysql://localhost:3306/test0?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8&useSSL=false";
    private static final String username = "root";
    private static final String password = "root";

    private static final String projectPath = System.getProperty("user.dir");

    private static final String auth = "Vincent";
    /**
     * 读取控制台内容
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotBlank(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(url, username, password).build();

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig.Builder()
                .outputDir(projectPath + "/src/main/java")
                .author(auth)
                .openDir(false)
                .build();

        // 包配置
        PackageConfig packageConfig = new PackageConfig.Builder("pers.vincent.vertxboot",scanner("模块名")).build();

        // 自定义配置
        InjectionConfig injectionConfig = new InjectionConfig
                .Builder()
                .beforeOutputFile(((tableInfo, stringObjectMap) -> {
                    // 自定义 Mapper XML 生成目录
                    ConfigBuilder config = (ConfigBuilder)stringObjectMap.get("config");
                    Map<String, String> pathInfoMap = config.getPathInfo();
                    pathInfoMap.put("xml_path", pathInfoMap.get("xml_path").replaceAll("/java.*","/resources/mapper"));
                    stringObjectMap.put("config", config);
                })).build();

        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig.Builder()
                // 表名
                .addInclude(scanner("表名，多个英文逗号分割").split(","))
                // Entity 策略配置
                .entityBuilder()
                // 开启 Lombok 模式
                .enableLombok()
                // 开启生成 serialVersionUID
                .enableSerialVersionUID()
                // 数据库表映射到实体的命名策略：下划线转驼峰
                .naming(NamingStrategy.underline_to_camel)
                // 主键策略为自动填充，默认 IdType.AUTO
                .idType(IdType.ASSIGN_ID)
                // Controller 策略配置
                .controllerBuilder()
                // 生成 @RestController 注解
//                .enableRestStyle()
                .build();

        // 模版配置
        TemplateConfig templateConfig = new TemplateConfig.Builder()
                .entity("/templates/entity.java")
                .mapper("/templates/mapper.java")
                .service("/templates/service.java", "/templates/serviceImpl.java")
                .controller("/templates/controller.java")
                .build();

        // 代码生成器
        AutoGenerator autoGenerator = new AutoGenerator(dataSourceConfig);
        autoGenerator.global(globalConfig);
        autoGenerator.packageInfo(packageConfig);
        autoGenerator.injection(injectionConfig);
        autoGenerator.strategy(strategyConfig);
        autoGenerator.template(templateConfig);
        autoGenerator.execute(new FreemarkerTemplateEngine());

    }

}
