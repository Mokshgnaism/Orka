package com.Orka.util;

import com.Orka.entities.condition.AtomicCondition;
import com.Orka.entities.condition.Condition;
import com.Orka.entities.condition.EvaluationContext;
import com.Orka.interfaces.Repository;

import java.util.*;

public final class ConditionEngine {
    private ConditionEngine() {}
    public static Boolean evaluateCondition(Condition condition,EvaluationContext evaluationContext,Repository repository){
        String expression = condition.getExpression();
        List<AtomicCondition> atomicCondtions = condition.getAtomicConditions();
        List<String>tokens = tokenize(expression);
        List<String>postfix = toPostfix(tokens);
        Map<String,Boolean>results = evaulateAtomicConditions(atomicCondtions,evaluationContext,repository);
        return evaulatePostfix(postfix,results);

    }
    private static Map<String,Boolean> evaulateAtomicConditions(
            List<AtomicCondition>atomicConditions,
            EvaluationContext context,
            Repository repository
    ){
        Map<String,Boolean> results = new HashMap<>();

        for(AtomicCondition atomicCondition: atomicConditions){
            results.put(atomicCondition.getName(),atomicCondition.evaluate(context,repository));
        }
        return results;
    }
    private static List<String>tokenize(String expression){
        return Arrays.stream(expression.split("\\s+")).toList();
    }
    private static int precedence(String token){
        return switch (token) {
            case "!" -> 3;
            case "&&" -> 2;
            case "||" -> 1;
            default -> 0;
        };
    }


    @org.jetbrains.annotations.NotNull
    private static List<String> toPostfix(List<String> tokens){

        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        for(String token: tokens){
            if(token.equals("(")){
                operators.push(token);
            }
            else if(token.equals(")")){
                while(!operators.peek().equals("(")){
                    output.add(operators.pop());
                }
                operators.pop();
            }
            else if(precedence(token) != 0){
                while(!operators.empty() && !operators.peek().equals("(") && precedence(operators.peek()) >= precedence(token)){
                    output.add(operators.pop());
                }
                operators.push(token);
            }else{
                output.add(token);
            }
        }
        while(!operators.empty()){
            output.add(operators.pop());
        }
        return output;
    }
    private static boolean evaulatePostfix(List<String>postFix,Map<String,Boolean> results){

        Stack<Boolean>stack = new Stack<>();
        for(String token: postFix){
            if(precedence(token) == 0){
                Boolean b = results.get(token);
                if(b == null){
                    throw new IllegalArgumentException("Invalid postfix expression");
                }
                stack.push(b);
            }
            else if(token.equals("!")){
                Boolean b = stack.pop();
                if(b == null){
                    throw new IllegalArgumentException("Invalid postfix expression");
                }
                stack.push(!b);
            }
            else{
                Boolean b1 = stack.pop();
                Boolean b2 = stack.pop();
                Boolean res = evaluateTwoAtomicConditions(b1,b2,token);
                stack.push(res);
            }
        }
        return stack.peek();
    }
    private static Boolean evaluateTwoAtomicConditions(Boolean b1,Boolean b2, String token){
        return switch (token) {
            case "&&" -> b2 && b1;
            case "||" -> b1 || b2;
            default -> throw new IllegalArgumentException("Operator not found we only support && || and !");
        };
    }
}
