package com.Orka.Assembler.WorkflowRunGraphAssembler;

import com.Orka.apiContract.generated.*;
import com.Orka.entities.condition.AtomicCondition;
import com.Orka.entities.condition.Condition;
import com.Orka.entities.condition.atomic.StateInCondition;
import com.Orka.entities.condition.atomic.StateInputCondition;
import com.Orka.entities.condition.atomic.StateOutputCondition;
import com.Orka.entities.condition.atomic.WorkflowVariableCondition;
import com.Orka.entities.runtime.StateRun;
import com.Orka.entities.runtime.TaskRun;
import com.Orka.entities.runtime.WorkflowRun;
import com.Orka.util.JsonUtility;
import com.Orka.util.ProtoEnumMapper;

import java.util.*;
import java.util.stream.Collectors;

public class WorkflowRunGraphAssembler {
    public static WorkflowRunGraph assemble(WorkflowRun workflowRun){
        List<Node>nodes=new ArrayList<>();
        List<Edge>edges=new ArrayList<>();
        Map<String,String> stateNameToId=new HashMap<>();
        for(var taskRun:workflowRun.getTaskRuns()){
            for(var stateRun:taskRun.getStateRuns()){
//                for each State, we need to add a state node 
                StateNode stateNode = toStateNode(stateRun);
                Node node = Node.newBuilder().setStateNode(stateNode).build();
                nodes.add(node);
                stateNameToId.put(stateNode.getStateName(), stateRun.getId().toString());
            }
        }
//        now add conditional edges 
        for(var taskRun:workflowRun.getTaskRuns()){
            for(var stateRun:taskRun.getStateRuns()){
                String joinNodeId = UUID.randomUUID().toString();
                JoinNode joinNode = JoinNode.newBuilder().setId(joinNodeId).build();


                Condition condition = stateRun.getStateDefinition().getConditionToBecomeActive();
                String expression = condition.getExpression();

                List<Edge> tempEdges = condition.getAtomicConditions().stream().map(atomicCondition -> BuildEdgeBetweenNodeToJoinNode(joinNodeId,atomicCondition,stateNameToId)).toList();
                if(tempEdges.isEmpty()){
                    continue;
//                   // no need to join the merge node to current state node.
//                    TODO : if only one condition no need to join that as well make sure that is correct .
                }
                edges.addAll(tempEdges);
                Edge JoinNodeToCurrentStateNode = Edge.newBuilder().
                        setFrom(joinNodeId).
                        setTo(stateRun.getId().toString()).
                        setPlaceHolder(
                                PlaceHolderCondition.newBuilder()
                                        .setName(condition.getName()!=null ? condition.getName() : "")
                                        .setExpression(expression)
                                        .build()
                        ).build();
                Node joinNodeHolder = Node.newBuilder().setJoinNode(joinNode).build();
                nodes.add(joinNodeHolder);
                edges.add(JoinNodeToCurrentStateNode);
            }
        }

        return WorkflowRunGraph.newBuilder().addAllEdges(edges).addAllNodes(nodes).build();
    }

    private static Edge BuildEdgeBetweenNodeToJoinNode(String toId, AtomicCondition atomicCondition,Map<String,String> stateNameToId){
        if(atomicCondition instanceof WorkflowVariableCondition)
            return null;
        if(atomicCondition instanceof StateInCondition){
            com.Orka.apiContract.generated.StateInCondition condition = com.Orka.apiContract.generated.StateInCondition.newBuilder().
                    setDestinationTaskDefinitionName(((StateInCondition) atomicCondition).getTaskDefinitionName())
                    .setRequiredStateDefinitionName(((StateInCondition) atomicCondition).getStateDefinitionName()).build();
            String fromStateId = stateNameToId.get(((StateInCondition) atomicCondition).getStateDefinitionName());
            return Edge.newBuilder().setFrom(fromStateId).setTo(toId).setStateIn(condition).build();
        }
        if(atomicCondition instanceof StateInputCondition){

            com.Orka.apiContract.generated.StateInputCondition condition = com.Orka.apiContract.generated.StateInputCondition.newBuilder()
                    .setComparisonOperator(ProtoEnumMapper.toProto(((StateInputCondition) atomicCondition).getOperator(),ComparisonOperator.class))
                    .setExpectedValue(JsonUtility.translateToProtobufValue(((StateInputCondition) atomicCondition).getExpectedValue()))
                    .setDestinationTaskDefinitionName(((StateInputCondition) atomicCondition).getReference().getTaskDefinitionName())
                    .setDataReference(
                            DataReference.newBuilder()
                                    .setStateInput(
                                            StateInputReference.newBuilder()
                                                    .setTaskDefinitionName(((StateInputCondition) atomicCondition).getReference().getTaskDefinitionName())
                                                    .setStateDefinitionName(((StateInputCondition) atomicCondition).getReference().getStateDefinitionName())
                                                    .setJsonPath(((StateInputCondition) atomicCondition).getReference().getJsonPath())
                                    )
                    ).build();
            String fromStateId = stateNameToId.get(((StateInputCondition) atomicCondition).getReference().getStateDefinitionName());
            return Edge.newBuilder().setFrom(fromStateId).setStateInput(condition).setTo(toId).setName(atomicCondition.getName()).build();
        }
        if (atomicCondition instanceof StateOutputCondition) {

            com.Orka.apiContract.generated.StateOutputCondition condition =
                    com.Orka.apiContract.generated.StateOutputCondition.newBuilder()
                            .setComparisonOperator(
                                    ProtoEnumMapper.toProto(
                                            ((StateOutputCondition) atomicCondition).getOperator(),
                                            ComparisonOperator.class))
                            .setExpectedValue(
                                    JsonUtility.translateToProtobufValue(
                                            ((StateOutputCondition) atomicCondition).getExpectedValue()))
                            .setDestinationTaskDefinitionName(
                                    ((StateOutputCondition) atomicCondition).getReference().getTaskDefinitionName())
                            .setDataReference(
                                    DataReference.newBuilder()
                                            .setStateOutput(
                                                    StateOutputReference.newBuilder()
                                                            .setTaskDefinitionName(
                                                                    ((StateOutputCondition) atomicCondition).getReference().getTaskDefinitionName())
                                                            .setStateDefinitionName(
                                                                    ((StateOutputCondition) atomicCondition).getReference().getStateDefinitionName())
                                                            .setJsonPath(
                                                                    ((StateOutputCondition) atomicCondition).getReference().getJsonPath())
                                                            .build())
                                            .build())
                            .build();

            String fromStateId =
                    stateNameToId.get(
                            ((StateOutputCondition) atomicCondition)
                                    .getReference()
                                    .getStateDefinitionName());

            return Edge.newBuilder()
                    .setFrom(fromStateId)
                    .setStateOutput(condition)
                    .setTo(toId)
                    .setName(atomicCondition.getName())
                    .build();
        }
        return null;
    }

    private static StateNode toStateNode(StateRun stateRun){
        return StateNode.newBuilder()
                .setStateId(stateRun.getId().toString())
                .setStateName(stateRun.getStateDefinition().getName())
                .setTaskName(stateRun.getTaskRun().getTaskDefinitionName())
                .setId(stateRun.getId().toString())
                .setInternalState(ProtoEnumMapper.toProto(stateRun.getStateDefinition().getInternalState(),OrkaInternalState.class))
                .setIsActive(stateRun.getTaskRun().getCurrentStateRun()==stateRun)
                .build();
    }
}
