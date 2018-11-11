package DecisionTree;

import Processor.DataReader;

/**
 * Created by olanre on 2018-10-28.
 */
public class DecisionNode {
        private String label ;
        private DataReader Attribute;

        public DataReader getAttribute() {
                return Attribute;
        }

        public void setAttribute(DataReader attribute) {
                Attribute = attribute;
        }


        public void setLabel(String label) {
                this.label = label;
        }


        public String getLabel() {
                return label;
        }

        public DecisionNode(DataReader A, String value){
                this.Attribute = A;
                this.label = value;
        }

        public DecisionNode(DataReader A){
                this.Attribute = A;
        }


        public DecisionNode( String value){
                this.label = value;
        }

        public DecisionNode(){
                this.label = null;
                this.Attribute = null;
        }



}
