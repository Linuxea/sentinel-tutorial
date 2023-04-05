package com.linuxea;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    // 配置规则.
    initFlowRules();

    while (true) {
      ContextUtil.enter("app");
      // 1.5.0 版本开始可以直接利用 try-with-resources 特性
      try (Entry ignored = SphU.entry("HelloWorld")) {
        // 被保护的逻辑
        printHelloWorld();
      } catch (BlockException ex) {
        // 处理被流控的逻辑
        System.out.println("blocked!");
      } finally {
        ContextUtil.exit();
      }
    }
  }

  public static void printHelloWorld() {
    System.out.println("hello world");
    ContextUtil.enter("app");
    try (Entry hi = SphU.entry("Hi")) {
      printHi();
    } catch (BlockException e) {
      System.out.println("blocked hi");
    } finally {
      ContextUtil.exit();
    }
  }

  public static void printHi() {
    System.out.println("hi");
  }


  private static void initFlowRules() {
    List<FlowRule> rules = new ArrayList<>();
    FlowRule rule = new FlowRule();
    rule.setResource("HelloWorld");
    rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
    // Set limit QPS to 20.
    rule.setCount(10000);
    rules.add(rule);

    FlowRule hIrule = new FlowRule();
    hIrule.setResource("Hi");
    hIrule.setGrade(RuleConstant.FLOW_GRADE_QPS);
    // Set limit QPS to 20.
    hIrule.setCount(2000);
    rules.add(hIrule);

    FlowRuleManager.loadRules(rules);
  }
}
