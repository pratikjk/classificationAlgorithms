package com.buffalo.cse.dm.classification.decisiontree;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeNode {
	 /** 
	   * An enumerator for classifying the structural type of a tree node
	   *  - Leaf nodes represents the ClassLabel and indication that no other descision is needed.
	   *  - Binary nodes represents a split into two possible values.
	   *  - Multi nodes represents a split into many(3 or more) possible values.
	   *  
	   */
	public enum Structure { LEAF, BINARY, MULTI };

	private Structure nodeStructure;
	
	public TreeNode(Structure struct){
		nodeStructure=struct;
	}
	
	public Structure getStructure(){
		return nodeStructure;
	}
	
	public abstract String toString();
	
	public static class Leaf extends TreeNode{
		
		private int classLabel;
		public Leaf() {
			super(Structure.LEAF);
		}
		
		public void setClassLabel(int classLabel){
			this.classLabel=classLabel;
		}
		
		public int getClassLabel(){
			return classLabel;
		}

		@Override
		public String toString() {
			return classLabel+"";
		}
		
	}
	
	
	public static class Binary extends TreeNode{

		public Binary() {
			super(Structure.BINARY);
		}
		
		private TreeNode lChild;
		private TreeNode rChild;
		
		private SplitModel splitCriteria;
		
		public void setLChild(TreeNode lchild){
			this.lChild=lchild;
		}
		
		public void setRChild(TreeNode rchild){
			this.rChild=rchild;
		}
		
		public TreeNode getLChild(){
			return this.lChild;
		}
		
		public TreeNode getRChild(){
			return this.rChild;
		}
		
		public void setSplitModel(SplitModel sm){
			splitCriteria=sm;
		}
		
		public SplitModel getSplitModel(){
			return splitCriteria;
		}

		@Override
		public String toString() {
			return this.getSplitModel().getSplitCriteria()+"\n{" +lChild.toString()+"} , " +
			        "{"+rChild.toString() + "}";
		}
		
	}
	
	
	public static class Multi extends TreeNode{
		
		public Multi(){
			this(3);
		}
		
		public Multi(int num){
			super(Structure.MULTI);
			numOfChildren=num;
			children=new ArrayList<TreeNode>(num);
		}
		
		private List<TreeNode> children;
		private int numOfChildren;
		
		public List<TreeNode> getChildren() {
			return children;
		}

		public void addChildren(TreeNode child) {
			children.add(child);
		}
		
		public TreeNode getChild(int i){
			return children.get(i);
		}

		public int getNumOfChildren() {
			return numOfChildren;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return null;
		}

		
	}

}
