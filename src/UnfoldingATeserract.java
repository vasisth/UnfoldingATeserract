import CubeObjects.*;
import CubeOperations.TreeNode;
import jdk.swing.interop.SwingInterOpUtils;
import org.w3c.dom.ls.LSOutput;

import java.sql.SQLOutput;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

public class UnfoldingATeserract {

		private static List<String> ans = new ArrayList<>();
		private static List<String> validUnfoldings = new ArrayList<>();
		private static String[][] grid;
		private static HashMap<String, Boolean> visitedEdges = new HashMap<>();
		private static HashMap<String, Boolean> visited = new HashMap<>();
		private static HashMap<String, List<String>> edgeToFaces = new HashMap<>();
		private static HashMap<String, List<Integer>> coordinatesOfTheFaces = new HashMap<>();
		private static HashMap<String, HashMap<String, String>> faceAndNeighbours = new HashMap<>();
		private static HashMap<String, String> directionArrows = new HashMap<>();
		private static HashMap<String, Integer> allTreeEncodings = new HashMap<>();
		private static HashMap<String, Integer> degree = new HashMap<>();
		private static Set<String> allEncodings = new HashSet<String>();



	public static void main(String[] args) {

		grid = new String[60][60];// grid to unfold onto
		List<Integer> startLoc = asList(30, 30);// start location
		// data given to edge to faces is wrong.
		HashMap<Integer, List<Face>> bipartiteGraph = CubeOperations.Cube.dualGraphOfTheCube();// edge->faces

		// creating an edge to face and vice verse.

		HashMap<String, List<String>> faceToEdges = new HashMap<>();
		List<String> edges = new ArrayList<>();
		// via these two we will traverse the cube.
		HashMap<String, List<Integer>> directions = new HashMap<>();// directions from the current face.

		directions.put("north", asList(-1, 0));
		directions.put("south", asList(1, 0));
		directions.put("west", asList(0, -1));
		directions.put("east", asList(0, 1));

		for (Integer i : bipartiteGraph.keySet()) {
			for (Face face : bipartiteGraph.get(i)) {
				if (!faceToEdges.containsKey(face.getTAG())) {
					faceToEdges.put(face.getTAG(), new ArrayList<>());
				}
				for (DualNode dn : face.getConnectedEdges())
					faceToEdges.get(face.getTAG()).add(dn.getTAG());
			}
		}

		for (String face : faceToEdges.keySet()) {
			for (String edge : faceToEdges.get(face)) {
				if (!edgeToFaces.containsKey(edge)) {
					edgeToFaces.put(edge, new ArrayList<>());
				}
				edgeToFaces.get(edge).add(face);
			}
		}

		for (String edge : edgeToFaces.keySet())
			edges.add(edge);

		HashMap<String, List<String>> edgeToFacesSolution = new HashMap<>();

		for (String edge : edgeToFaces.keySet())
			edgeToFacesSolution.put(edge, new ArrayList<>());
		AbstractCube abstractCube = new AbstractCube();
		//System.out.println(faceToEdges.toString());
		traversalWithBacktracking(faceToEdges, abstractCube);
		//System.out.println(allEncodings.toString());
		System.out.println("THE END MY BOIIII");
		//System.out.println("THE NNUMBER OF SOLUTIONS IS: "+numberofsol);
		// traverseTheCube(edgeToFaces, edges, 0, edgeToFacesSolution, faceToEdges);

		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				grid[i][j] = "-";
			}
		}




	}


	public static void traversalWithBacktracking(HashMap<String, List<String>> faceToEdges, AbstractCube abstractCube) {
		/**
		 To create a tree.
		 1.every time you place a face on the grid attach it in the tree too.
		 to do that we will crete a hashmap(tag->TreeNode) of all the nodes. attach the child to the parent and set the parent too.
		 once you have done this and on the return operation free the node of parent and the neighbour. oblivoulsy the parent will be the
		 last guy to get removed.

		 * */
		//creating a hashmap of tag to its object.
		List<List<Integer>> tree = new ArrayList<>();
		HashMap<String, Integer> nodeIndex = new HashMap<>(24);

		int i=0;

		for(String face: faceToEdges.keySet()){
			nodeIndex.put(face, i);
			tree.add(new ArrayList<>());
			i++;
			faceAndNeighbours.put(face, new HashMap<>());
		}




		// step1: fix the leaf node and join it with the other face.



		directionArrows.put("NORTH", "^");
		directionArrows.put("SOUTH", "v");
		directionArrows.put("EAST", ">");
		directionArrows.put("WEST", "<");

		// preparing the visited hashmap.
		for (String face : faceToEdges.keySet()) {
			visited.put(face, false);

		}
		for(String edge: edgeToFaces.keySet()){
			visitedEdges.put(edge, false);
		}

		//assigning leaf face.
		String leafFace="FEGH";
		System.out.println(faceToEdges.toString());
		for (String face : faceToEdges.keySet()) {
			//if(face.contains('G')&&face.contains('F')&&face.contains('H')){
			leafFace = face;
			break;}


		//checks for correctness of leaf face.
		if (leafFace == "") {
			System.out.println("Could not find a leafFace.");
			System.exit(0);
		}

//        int removeConnections = 0;
//        for(String edge: faceToEdges.get(leafFace)){//removing three connection with the leadFace.
//            if(removeConnections++==3)break;
//            edgeToFaces.get(edge).remove(leafFace);
//        }
		// call here

		// defining the directions of the leaf node.
        //copied the function from the top.
		String centralFace = leafFace;
		HashMap<String, String> currDirections = new HashMap<>(4);

		// adding the north edge
		currDirections.put("NORTH", faceToEdges.get(centralFace).get(0));
		boolean flipSides = true;
		//assigning directions to the start face
		for (String edge : faceToEdges.get(centralFace)) {
			if (edge == currDirections.get("NORTH"))
				continue;

			if ((edge.charAt(0) != currDirections.get("NORTH").charAt(0)
					&& edge.charAt(0) != currDirections.get("NORTH").charAt(1))
					&& (edge.charAt(1) != currDirections.get("NORTH").charAt(0)
							&& edge.charAt(1) != currDirections.get("NORTH").charAt(1))) {
				// adding the south edge.
				currDirections.put("SOUTH", edge);
			} else {
				// adding the east or west edge
				
				if (flipSides) {
					currDirections.put("EAST", edge);
					flipSides = false;
				} else
					currDirections.put("WEST", edge);

			}
		}
		System.out.println("The directions of the central face is: "+currDirections.toString());
		// getting the other face the central face is connected to.
		String otherFace = "";
		String parentEdge = "";

		for (String edge : faceToEdges.get(leafFace)) {// removing three connection with the leadFace.
			if (edgeToFaces.get(edge).contains(centralFace)) {
				parentEdge = edge;
				otherFace = leafFace == edgeToFaces.get(edge).get(0) ? edgeToFaces.get(edge).get(2)
						: edgeToFaces.get(edge).get(0);
			}


		}

		if (otherFace == "") {
			System.out.println("Could not find the other face");
			System.exit(0);
		}




		Stack<String> theQueueParent = new Stack<>();
		Stack<String> theQueueChildren = new Stack<>();


		// add the info about the parent in the abstract cube and do the needful for the
		// child face aka otherFace.
		// operations happening at each face.
		/*
		 *
		 * done: String self = theQueue.remove();// this is the face we are dealing with
		 * rn. done: HashMap<String, String> selfDirections =
		 * AbstractCube.getSelfDirections(self, faceToEdges);// note you have been put
		 * at your place by your parent and he also reginsted your directions. you are
		 * just getting it. done: String parentSharingEdge=
		 * AbstractCube.getParentSharingEdge(self);//store key value as key being the
		 * child and the value the edge that the child and parent share. List<Integer>
		 * myCoordinates = AbstractCube.getCoordinatesOfTheFaces(self);//[x,y]
		 * coordinates HashMap<String, List<Integer>> dir =
		 * AbstractCube.getUnitVectors();//this returns a hash table of NORTH -> (0,1).
		 * List<String> availableDirections = new ArrayList<>();
		 *
		 *
		 */
		String parentDir = "";
		for (String dir : currDirections.keySet()) {
			if (currDirections.get(dir) == parentEdge) {
				parentDir = dir;
			}
		}
		//System.out.println("parent dirrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr: "+ parentDir);
		// marking both the faces as visited.
		visitedEdges.put(parentEdge, true);
		visited.put(leafFace, true);
		visited.put(otherFace, true);
		// putting the faces on the grid.
		for ( i = 0; i < 60; i++) {
			for (int j = 0; j < 60; j++) {

				grid[i][j] = "-";
			}
		}
		grid[30][30] = "F";
		grid[30 + abstractCube.getUnitVectors().get(parentDir).get(0)][30
				+ abstractCube.getUnitVectors().get(parentDir).get(1)] = "f";//otherFace;

		System.out.println("the parent face from main " + leafFace);
		System.out.println("the other face is: " + otherFace);
		AbstractCube.addEdgeToParent(parentEdge, leafFace);// this is correct.
		theQueueParent.add(otherFace);
//		System.out.println("CALL FROM THE FUNCTION TRAVERSAL WITH BACKTRACKING: SUBMITTING FACE " + centralFace
//				+ " directions of the face is: " + currDirections.toString());

		AbstractCube.addToFaceAndItsDirections(centralFace, currDirections);// this is correct.
		AbstractCube.addFaceToParentSharingEdge(otherFace, parentEdge);
		System.out.println("placing otherface in the direction of " + parentDir
				+ " to 30,30 and the coordinates are these" + 30 + abstractCube.getUnitVectors().get(parentDir).get(0)
				+ 30 + abstractCube.getUnitVectors().get(parentDir).get(1));

		//other face coordinates.
		AbstractCube.addToCoordinatesOfTheFaces(otherFace,
				asList(30 + abstractCube.getUnitVectors().get(parentDir).get(0),
						30 + abstractCube.getUnitVectors().get(parentDir).get(1)));

			//tree operations.

		faceAndNeighbours.get(leafFace).put(parentDir, otherFace);
		faceAndNeighbours.get(otherFace).put(oppositeDirection(parentDir), leafFace);



		tree.get(nodeIndex.get(centralFace)).add(nodeIndex.get(otherFace));
		tree.get(nodeIndex.get(otherFace)).add(nodeIndex.get(centralFace));

		Map<String, String>  leafAndItsParent = new HashMap<>();
		leafAndItsParent.put(leafFace, otherFace);

		String s=BFS(theQueueParent, theQueueChildren, faceToEdges, abstractCube, tree, nodeIndex, otherFace, leafAndItsParent);
		System.out.println(s+" output     "+depth);
		//tree operation post traversal.
		tree.get(nodeIndex.get(centralFace)).remove(nodeIndex.get(otherFace));
		tree.get(nodeIndex.get(otherFace)).remove(nodeIndex.get(centralFace));

	}



	private static int faceNumber = 1;
	private static int numberofsol=0;
	private static int depth=0;
	static int numberofUniqueSol= 1;
	public static int index=1;
	public static String BFS(Stack<String> theQueueParent, Stack<String> theQueueChildren, HashMap<String, List<String>> faceToEdges,
						   AbstractCube abstractCube, List<List<Integer>> tree, HashMap<String, Integer> nodeIndex, String otherFace, Map<String, String>leafAndItsParent) {
		//System.out.println("REACHED BFS");

		for (int col = 0; col < 60; col++) {
			for (int row = 0; row < 60; row++) {
				if (col == 30 && row == 30) {
					System.out.print("F");
					continue;
				}
				if (col == 0) {
					if (row % 10 == 0) {

						System.out.print(row / 10);
					} else System.out.print(grid[col][row]);
				} else {
					System.out.print(grid[col][row]);
				}
			}
			System.out.println();
			System.out.print(col + 1);
		}




		if (theQueueParent.size() == 0) {

			for (int col = 0; col < 60; col++) {
				for (int row = 0; row < 60; row++) {
					if (col == 30 && row == 30) {
						System.out.print("F");
						continue;
					}
					if (col == 0) {
						if (row % 10 == 0) {

							System.out.print(row / 10);
						} else System.out.print(grid[col][row]);
					} else {
						System.out.print(grid[col][row]);
					}
				}
				System.out.println();
				System.out.print(col + 1);
			}
			for (String face : visited.keySet()) {
				System.out.println("BREAKING HERE YOO HOO");
				if (!visited.get(face)) {
					//System.out.println("NNNNNNOOOOOOOOOOOOOOOOOOOONNNNNNNNOOOOOOOOOO did not visit all faces");
					return "AllOk";
				}
			}
			System.out.println(leafAndItsParent.toString());
			System.out.println(degree.toString());
		//	System.out.println(numberofsol++ +"YEEEEEEEEEEEEEEEEEEEEEEEEEEAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHH...... REACHED THE END.");
			//grid[35][28]=="FGfg> "
			//grid[32][35].equals("fgFG") && !grid[32][36].equals("-");//

			//fgbc> fgFG> ghGH>
			//grid[32][34].equals("fgbcv ") && grid[32][35].equals("fgFG> ") && grid[33][35].equals("ghGHv ") && grid[35][37].equals("AaEev ") && grid[32][37].equals("cgCG^ ") && grid[30][34].equals("dahe^ ")
			//grid[30][28].equals("-")||grid[29][29].equals("-")||grid[31][29].equals("-")

			for(String s: leafAndItsParent.keySet()){

				if(degree.get(leafAndItsParent.get(s))>1) return leafAndItsParent.get(s);

			}

				CheckForUniqueShape(tree, nodeIndex);

			//if(allEncodings.size()==99){
				System.out.println();

				for (int col = 0; col < 60; col++) {
					for (int row = 0; row < 60; row++) {
						if (col == 30 && row == 30) {
							System.out.print("F");
							continue;
						}
						if (col == 0) {
							if (row % 10 == 0) {

								System.out.print(row / 10);
							} else System.out.print(grid[col][row]);
						} else {
							System.out.print(grid[col][row]);
						}
					}
					System.out.println();
					System.out.print(col + 1);
				}

				System.exit(0);
				return "AllOk";
			}


		//}



        String self = theQueueParent.pop();// this is the face we are dealing with rn.
		degree.put(self, 0);

        boolean spotFilledByI = false, spotFilledByJ = false, spotFilledByZ = false;
        String parentDirection =""; //north west east south

        HashMap<String, String> selfDirections = abstractCube.getSelfDirections(self, faceToEdges);// note you have been

        String parentSharingEdge = abstractCube.getParentSharingEdge(self);// store key value as key being the child and
        // the value the edge that the child and
        // parent share.
		String parent = AbstractCube.getEdgeToParent().get(parentSharingEdge);
        List<Integer> myCoordinates = abstractCube.getCoordinatesOfTheFaces(self);// [x,y] coordinates
        HashMap<String, List<Integer>> dir = abstractCube.getUnitVectors();// this returns a hash table of NORTH ->
        List<String> edgesExploredByTheCurrFace = new ArrayList<>();
		// (0,1).
        List<String> availableDirections = new ArrayList<>();
       // System.out.println("my coordinates: "+ myCoordinates.toString());
        //System.out.println("parentSharing edge: "+ parentSharingEdge);
		// finding the available directions to explore further.
		for (String direction : selfDirections.keySet()) {
			// System.out.println("----------");
			// System.out.println(" self directions running now: "+ direction);
			if (selfDirections.get(direction) == parentSharingEdge) {
				// System.out.println("parent sharing edge direction: "+direction);
				parentDirection = direction;
				continue;
			}
			//
			// System.out.println(" this is added to the available faces "+ direction);
			availableDirections.add(direction);
			// System.out.println("edge to parent registered with edge "+
			// selfDirections.get(direction)+ "and parent "+ self);
//			if(!visitedEdges.get(selfDirections.get(direction)) ){
//				visitedEdges.put(selfDirections.get(direction), true);
//				edgesExploredByTheCurrFace.add(selfDirections.get(direction));
//			AbstractCube.addEdgeToParent(selfDirections.get(direction), self);}
		}

		// creating combinations of
		int myDegree=1;
		int countI = 0;
		for (int i = 2; i >= 0; i--) {
			String face1 = "";
			spotFilledByI = false;

			if (i != 0) {

				if (grid[myCoordinates.get(0) + dir.get(availableDirections.get(0)).get(0)][myCoordinates.get(1)
						+ dir.get(availableDirections.get(0)).get(1)] != "-") {
					//System.out.println("the spot is taken in the grid");
					continue;
				}


				// getting the faces in this.
				while (countI < 3) {
					if (edgeToFaces.get(selfDirections.get(availableDirections.get(0))).get(countI) == self) {
						countI++;
					} else {
						face1 = edgeToFaces.get(selfDirections.get(availableDirections.get(0))).get(countI);
						countI++;
						break;
					}
				}
				if (face1 == self || face1 == "") {
					System.out.println(
							"Did not get the correct neighbour face at i= " + i + " the face obtains is: " + face1);
					System.exit(0);
				}

				if (visited.get(face1) || visitedEdges.get(selfDirections.get(availableDirections.get(0)))) {
					//System.out.println("face visited at i level" + face1+ " the vaue of i = "+ i);
					continue;
				}
				AbstractCube.addEdgeToParent(selfDirections.get(availableDirections.get(0)), self);

				// there are some operations thw parent has to do before putting the face in
				// theQueue.
				//remove this as you need to update this at the end of each loop.
				AbstractCube.addFaceToParentSharingEdge(face1, selfDirections.get(availableDirections.get(0)));
				AbstractCube.addToCoordinatesOfTheFaces(face1,
						asList(myCoordinates.get(0) + dir.get(availableDirections.get(0)).get(0),
								myCoordinates.get(1) + dir.get(availableDirections.get(0)).get(1)));

				visited.put(face1, true);

				visitedEdges.put(selfDirections.get(availableDirections.get(0)), true);
				degree.put(self, degree.get(self)+1);
				//System.out.println("Parent " + self + " submitted this face at i level to the queue " + face1);
				theQueueChildren.add(face1);
				grid[myCoordinates.get(0) + dir.get(availableDirections.get(0)).get(0)][myCoordinates.get(1)
						+ dir.get(availableDirections.get(0)).get(1)] =depth +face1 +directionArrows.get(availableDirections.get(0))+" ";
				//tree operations.
				tree.get(nodeIndex.get(self)).add(nodeIndex.get(face1));
				tree.get(nodeIndex.get(face1)).add(nodeIndex.get(self));
				faceAndNeighbours.get(self).put(availableDirections.get(0), face1);
				faceAndNeighbours.get(face1).put(oppositeDirection(availableDirections.get(0)), self);


				spotFilledByI = true;

			}

			int countJ = 0;
			for (int j = 2; j >= 0; j--) {
				String face2 = "";
				spotFilledByJ = false;
				if (j != 0 )//&& edgesExploredByTheCurrFace.contains(selfDirections.get(availableDirections.get(1)))) {
				{
					if (grid[myCoordinates.get(0) + dir.get(availableDirections.get(1)).get(0)][myCoordinates.get(1)
							+ dir.get(availableDirections.get(1)).get(1)] != "-")
						continue;

					while (countJ < 3) {
						if (edgeToFaces.get(selfDirections.get(availableDirections.get(1))).get(countJ) == self) {
							countJ++;
						} else {
							face2 = edgeToFaces.get(selfDirections.get(availableDirections.get(1))).get(countJ);
							countJ++;
							break;
						}
					}
					if (face2 == self || face2 == "") {
						System.out.println(
								"Did not get the correct neighbour face at j= " + j + " the face obtains is: " + face2);
						System.exit(0);
					}
					if (visited.get(face2) || visitedEdges.get(selfDirections.get(availableDirections.get(1)))) {
						//System.out.println("face2 "+ face2+" visited prior at j= "+j);
						continue;
					}
					AbstractCube.addEdgeToParent(selfDirections.get(availableDirections.get(1)), self);

					visited.put(face2, true);
					visitedEdges.put(selfDirections.get(availableDirections.get(1)), true);
					degree.put(self, degree.get(self)+1);
					theQueueChildren.add(face2);
					//System.out.println("Parent " + self + " submitted this face at j level to the queue " + face2);
					// there are some operations thw parent has to do before putting the face in
					// theQueue.
					AbstractCube.addFaceToParentSharingEdge(face2, selfDirections.get(availableDirections.get(1)));
					AbstractCube.addToCoordinatesOfTheFaces(face2,
							asList(myCoordinates.get(0) + dir.get(availableDirections.get(1)).get(0),
									myCoordinates.get(1) + dir.get(availableDirections.get(1)).get(1)));

					grid[myCoordinates.get(0) + dir.get(availableDirections.get(1)).get(0)][myCoordinates.get(1)
							+ dir.get(availableDirections.get(1)).get(1)] = depth+ face2+directionArrows.get(availableDirections.get(1))+" ";
					//tree operations.

					tree.get(nodeIndex.get(self)).add(nodeIndex.get(face2));
					tree.get(nodeIndex.get(face2)).add(nodeIndex.get(self));
					faceAndNeighbours.get(self).put(availableDirections.get(1), face2);
					faceAndNeighbours.get(face2).put(oppositeDirection(availableDirections.get(1)), self);

					spotFilledByJ = true;
				}

				int countZ = 0;
				for (int z = 2; z >= 0; z--) {
					//System.out.println("Entered z: "+ z);
					String face3 = "";
					spotFilledByZ = false;
					// System.out.println("THE Z VALUE"+ z);

					if (z != 0 ) {
						if(otherFace==self && parentDirection!=oppositeDirection(availableDirections.get(1)))continue;
						if (grid[myCoordinates.get(0) + dir.get(availableDirections.get(2)).get(0)][myCoordinates.get(1)
								+ dir.get(availableDirections.get(2)).get(1)] != "-")
						{
							//System.out.println("Location full: can't unfold there.");
							continue; }


						while (countZ < 3) {

							if (edgeToFaces.get(selfDirections.get(availableDirections.get(2))).get(countZ) == self) {
								countZ++;
							} else {
								face3 = edgeToFaces.get(selfDirections.get(availableDirections.get(2))).get(countZ);
								countZ++;
								break;
							}
						}
						if (face3 == self || face3 == "") {
							System.out.println("Did not get the correct neighbour face at z= " + i
									+ " the face obtains is: " + face3);
							System.exit(0);
						}
						if (visited.get(face3)|| visitedEdges.get(selfDirections.get(availableDirections.get(2))) ) {
							//System.out.println("face3 "+ face3+" visited prior");
							continue;
						}
						AbstractCube.addEdgeToParent(selfDirections.get(availableDirections.get(2)), self);
						visited.put(face3, true);
						visitedEdges.put(selfDirections.get(availableDirections.get(2)), true);
						degree.put(self, degree.get(self)+1);
						theQueueChildren.add(face3);
						//System.out.println("Parent " + self + " submitted this face at z level to the queue " + face3);
								AbstractCube.addFaceToParentSharingEdge(face3, selfDirections.get(availableDirections.get(2)));
						AbstractCube.addToCoordinatesOfTheFaces(face3,
								asList(myCoordinates.get(0) + dir.get(availableDirections.get(2)).get(0),
										myCoordinates.get(1) + dir.get(availableDirections.get(2)).get(1)));

						grid[myCoordinates.get(0) + dir.get(availableDirections.get(2)).get(0)][myCoordinates.get(1)
								+ dir.get(availableDirections.get(2)).get(1)] =depth+ face3+directionArrows.get(availableDirections.get(2))+" ";;

						//tree operations.
						tree.get(nodeIndex.get(self)).add(nodeIndex.get(face3));
						tree.get(nodeIndex.get(face3)).add(nodeIndex.get(self));
						faceAndNeighbours.get(self).put(availableDirections.get(2), face3);
						faceAndNeighbours.get(face3).put(oppositeDirection(availableDirections.get(2)), self);



						spotFilledByZ = true;
					}

						if(self==otherFace && ( j !=0 || z!=0)){
							//for z:
							if(z!=0){
								visited.put(face3, false);
								grid[myCoordinates.get(0) + dir.get(availableDirections.get(2)).get(0)][myCoordinates.get(1)
										+ dir.get(availableDirections.get(2)).get(1)] = "-";
								visitedEdges.put(selfDirections.get(availableDirections.get(2)), false);
								degree.put(self, degree.get(self) - 1);
								AbstractCube.removeEdgeToParent(selfDirections.get(availableDirections.get(2)));
								AbstractCube.removeFaceToParentSharingEdge(face3);
								AbstractCube.removeCoordinatesOfTheFaces(face3);
								theQueueChildren.remove(face3);

								//tree operation post traversal.
								tree.get(nodeIndex.get(self)).remove(nodeIndex.get(face3));
								tree.get(nodeIndex.get(face3)).remove(nodeIndex.get(self));
								faceAndNeighbours.get(self).remove(availableDirections.get(2), face3);
								faceAndNeighbours.get(face3).remove(oppositeDirection(availableDirections.get(2)), self);
							}
							//for j.
							if(j!=0){
								visited.put(face2, false);
								grid[myCoordinates.get(0) + dir.get(availableDirections.get(1)).get(0)][myCoordinates.get(1)
										+ dir.get(availableDirections.get(1)).get(1)] = "-";
								visitedEdges.put(selfDirections.get(availableDirections.get(1)), false);
								degree.put(self, degree.get(self) - 1);
								AbstractCube.removeEdgeToParent(selfDirections.get(availableDirections.get(1)));
								AbstractCube.removeFaceToParentSharingEdge(face2);
								AbstractCube.removeCoordinatesOfTheFaces(face2);
								theQueueChildren.remove(face2);
								//tree operations

								tree.get(nodeIndex.get(self)).remove(nodeIndex.get(face2));
								tree.get(nodeIndex.get(face2)).remove(nodeIndex.get(self));
								faceAndNeighbours.get(self).remove(availableDirections.get(1), face2);
								faceAndNeighbours.get(face2).remove(oppositeDirection(availableDirections.get(1)), self);
							}

							continue;
						}

						else {if(!spotFilledByI && !spotFilledByJ && !spotFilledByZ){

							if(degree.get(parent)>1)return parent;
						}

							depth++;
							if (theQueueParent.size() != 0) {
								String s = BFS(theQueueParent, theQueueChildren, faceToEdges, abstractCube, tree, nodeIndex, otherFace, leafAndItsParent);
								if(leafAndItsParent.containsKey(self))leafAndItsParent.remove(self, parent);
								if(s!="AllOk"){
									if(s==self){
										continue;
									}
									else return s;
								}
								else return s;
							}
							else {
								String s = BFS(theQueueChildren, theQueueParent, faceToEdges, abstractCube, tree, nodeIndex, otherFace, leafAndItsParent);
								if(leafAndItsParent.containsKey(self))leafAndItsParent.remove(self, parent);
								if(s!="AllOk"){
									if(s==self){
										continue;
									}
								}
								else return s;

							}
							depth--;

						}

					if (spotFilledByZ) {
						visited.put(face3, false);
						grid[myCoordinates.get(0) + dir.get(availableDirections.get(2)).get(0)][myCoordinates.get(1)
								+ dir.get(availableDirections.get(2)).get(1)] = "-";
						visitedEdges.put(selfDirections.get(availableDirections.get(2)), false);
						degree.put(self, degree.get(self)-1);
						AbstractCube.removeEdgeToParent(selfDirections.get(availableDirections.get(2)));
						AbstractCube.removeFaceToParentSharingEdge(face3);
						AbstractCube.removeCoordinatesOfTheFaces(face3);
						theQueueChildren.remove(face3);

						//tree operation post traversal.
						tree.get(nodeIndex.get(self)).remove(nodeIndex.get(face3));
						tree.get(nodeIndex.get(face3)).remove(nodeIndex.get(self));
						faceAndNeighbours.get(self).remove(availableDirections.get(2), face3);
						faceAndNeighbours.get(face3).remove(oppositeDirection(availableDirections.get(2)), self);



					}

				}

				if (spotFilledByJ) {
					visited.put(face2, false);
					grid[myCoordinates.get(0) + dir.get(availableDirections.get(1)).get(0)][myCoordinates.get(1)
							+ dir.get(availableDirections.get(1)).get(1)] = "-";
					visitedEdges.put(selfDirections.get(availableDirections.get(1)), false);
					degree.put(self, degree.get(self)-1);
					AbstractCube.removeEdgeToParent(selfDirections.get(availableDirections.get(1)));
					AbstractCube.removeFaceToParentSharingEdge(face2);
					AbstractCube.removeCoordinatesOfTheFaces(face2);
					theQueueChildren.remove(face2);
					//tree operations

					tree.get(nodeIndex.get(self)).remove(nodeIndex.get(face2));
					tree.get(nodeIndex.get(face2)).remove(nodeIndex.get(self));
					faceAndNeighbours.get(self).remove(availableDirections.get(1), face2);
					faceAndNeighbours.get(face2).remove(oppositeDirection(availableDirections.get(1)), self);


				}
			}

			if (spotFilledByI) {
				visited.put(face1, false);
				grid[myCoordinates.get(0) + dir.get(availableDirections.get(0)).get(0)][myCoordinates.get(1)
						+ dir.get(availableDirections.get(0)).get(1)] = "-";
				visitedEdges.put(selfDirections.get(availableDirections.get(0)), false);
				degree.put(self, degree.get(self)-1);
				AbstractCube.removeEdgeToParent(selfDirections.get(availableDirections.get(0)));
				AbstractCube.removeFaceToParentSharingEdge(face1);
				AbstractCube.removeCoordinatesOfTheFaces(face1);
				theQueueChildren.remove(face1);
				//tree operations.
				tree.get(nodeIndex.get(self)).remove(nodeIndex.get(face1));
				tree.get(nodeIndex.get(face1)).remove(nodeIndex.get(self));
				faceAndNeighbours.get(self).remove(availableDirections.get(0), face1);
				faceAndNeighbours.get(face1).remove(oppositeDirection(availableDirections.get(0)), self);


			}
		}

		for(String edge: edgesExploredByTheCurrFace)
		{	//visitedEdges.put(edge,false);
			AbstractCube.removeEdgeToParent(edge);

		}
		AbstractCube.removeFaceAndItsDirections(self);
		degree.remove(self, 0);


		//System.out.println(self+"  THEEEEEEEEEEEEEEEEEEEEEEE EEEEEEEEEEEEEEEEEEEEEEEENNNNNNNNNNNNNNNNNNNNNNNNNNNDDDDDDDDDDDDDDDDDDDD");
	return "AllOk";
	}




	/**
	 *1. find the center of the tree and  decide the highest number of node  in any particular direction.
	 * 2. define this direction as north and choose west as the highest from the remaining available directions.
	 *
	 */

	private static void CheckForUniqueShape( List<List<Integer>> tree, HashMap<String, Integer> nodeIndex){

		//System.out.println("Reached check for unique solutions");


		boolean ans= treesAreIsomorphic(tree, nodeIndex);
//
//		if(allEncodings.size()>261){
//			System.out.println("ALL ENCODINGS SIZE IS GREATER THAN 261");
//			System.exit(0);
//		}
		//System.out.println("ALL ENCODINGS SIZE :"+allEncodings.size());
//		if(ans){
//			//System.out.println("ANOTHER ISOMORPHIC AND THE NUMBER OF UNIQUE UNFOLDINGS ARE: "+ numberofUniqueSol);
//
//		}else{
//			//System.out.println("NEW SHAPE FOUND AND THE NUMBER OF UNIQUE UNFOLDINGS ARE: "+ numberofUniqueSol);
//		}


		return;
	}

	public static boolean treesAreIsomorphic(List<List<Integer>> tree, HashMap<String, Integer> nodeIndex) {
		List<Integer> seen = new ArrayList<>();
		for(int i=0; i<24; i++)seen.add(0);

		HashMap<Integer, String> nodeIndexReverse  = new HashMap<>();
		for(String face: nodeIndex.keySet()){
			nodeIndexReverse.put(nodeIndex.get(face), face);
		}

		//find the center of the tree. this will be a list of nodes.
		// do a dfs on the tree and find the direction in which the number of nodes in all the directions.
		//this will be a list of nodes.
		List<Integer> centers = findTheCenterOfTheTree(tree, nodeIndex);
		HashMap<String,Integer> depths = new HashMap<>(4);
		depths.put("NORTH", 0);
		depths.put("SOUTH", 0);
		depths.put("WEST", 0);
		depths.put("EAST", 0);

		String encoding="";

		for(Integer center: centers){
			seen.set(center, 1);
			HashMap<String, String> centerDirection = AbstractCube.getFaceAndItsDirections().get(nodeIndexReverse.get(center));
			//grid[AbstractCube.getCoordinatesOfTheFaces(nodeIndexReverse.get(center)).get(0)][AbstractCube.getCoordinatesOfTheFaces(nodeIndexReverse.get(center)).get(1)]="C";
			for(String dir: faceAndNeighbours.get(nodeIndexReverse.get(center)).keySet()) {
				if(!faceAndNeighbours.get(nodeIndexReverse.get(center)).containsKey(dir))continue;
				//System.out.println(" neighbouring face in the direction: "+"  "+dir);
				int nodeToExplore = nodeIndex.get(faceAndNeighbours.get(nodeIndexReverse.get(center)).get(dir));

				int nodeVal =0;
				//tree is correct till here.
				//System.out.println("node to exploree :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: "+ nodeToExplore);
				int depth = findNumberOfNodes(tree, center, nodeToExplore, seen);
				depths.put(dir, depth);
			}

			List<String> north= new ArrayList<>();
			List<String> west = new ArrayList<>();
			String maxDir = "";
			int maxNumberOfNodes=-1;
			// the highest number of nodes in a particular direction
			for(String dir: depths.keySet()){
				if(maxNumberOfNodes<depths.get(dir)) maxDir = dir;
			}

			north.add(maxDir);

			for(String dir: depths.keySet()){
				if(depths.get(dir)==depths.get(maxDir) && !maxDir.equals(dir))
					north.add( dir);
			}
			//taken all possible norths.
			//now for each north we have to permute and find the correct west direction and perform the the encoding on that.
			//find the west direction.
			Set<String> set;//this set will contain
			for(String dir: north){
				set = new HashSet<String>();
				set.add(dir);
				set.add(oppositeDirection(dir));

				for(String dir2: depths.keySet()){
					if(!set.contains(dir2)){
						if(depths.get(dir2)!=depths.get(oppositeDirection(dir2))){
							west.add(depths.get(dir2)>depths.get(oppositeDirection(dir2))?dir2:oppositeDirection(dir2));

						}else {
							west.add(dir2);
							west.add(oppositeDirection(dir2));

						}
						break;
					}

				}
				for(String directionWest: west){
					//code is a will return a string of encoding.
					Set<String> visitedFaces = new HashSet<>();

					encoding = encode(dir, directionWest, nodeIndexReverse.get(center), visitedFaces, 0);
					//System.out.println("NEW ENCODING AND ITS MATRIX BELOW.");
					//System.out.println("THE DEPTH OF TREES IN EACH DIRECTIONS IS: "+depths.toString());
					//System.out.println("The new West is the old "+west.toString()+" new north is the old: "+ north.toString()+" center is"+centers.toString());

					//System.out.println(encoding);
					//System.out.println("The center is: "+ nodeIndexReverse.get(center)+" the new north is: "+ dir+ " the new west is: "+ directionWest+ " FYI: the encoding pattern{north,south,east, west} these. These directions are the new directions");
//					for(int i=0; i<60; i++){
//						for(int j =0; j<60; j++){
//							System.out.print(grid[i][j]);
//						}
//						System.out.println();
//					}

					//create a set of these encodings and check if it is present.
					if(allEncodings.contains(encoding)){
//
//						System.out.println(encoding);
//						for(int i=0; i<60; i++){
//						for(int j =0; j<60; j++){
//							System.out.print(grid[i][j]);
//						}
//						System.out.println();
//					}
//						System.exit(0);
						return true;
					}

				}





			}



			//AbstractCube.getFaceAndItsDirections().toString();

//		String encoding = encodeTree(tree, nodeIndex);
//		if(allTreeEncodings.containsKey(encoding)) return true;
//		allTreeEncodings.put(encoding, numberofUniqueSol++);
//		return false;

		}
		//System.out.println("Adding an encoding "+ encoding);
		allEncodings.add(encoding);
		//System.out.println("Returning from the treesAreIsometric function");
		return false;
	}

	public static String encode(String northDir,String westDir, String center, Set<String> visitedFaces, int level){
		//System.out.println("IN ENCODING FUNCTION");
		visitedFaces.add(center);
		HashMap<String, String> resultFromEachDirection = new HashMap<>();
		resultFromEachDirection.put("NORTH", "~");
		resultFromEachDirection.put("SOUTH", "~");
		resultFromEachDirection.put("EAST", "~");
		resultFromEachDirection.put("WEST", "~");
		boolean flag= false;
		for(String direction :faceAndNeighbours.get(center).keySet()){
			if(!visitedFaces.contains(faceAndNeighbours.get(center).get(direction))){

				resultFromEachDirection.put(direction, encode(northDir, westDir, faceAndNeighbours.get(center).get(direction), visitedFaces, level+1));
				flag = true;
			}
		}
		if(flag){
			String s ="("+ resultFromEachDirection.get(northDir)+resultFromEachDirection.get(oppositeDirection(northDir))+resultFromEachDirection.get(oppositeDirection(westDir))+resultFromEachDirection.get(westDir)+")";

			//	System.out.println(s+ level);
			//System.out.println("Returning from the encoding function");
			return s;
		}
	//	System.out.println("Returning from the encoding function");
		//System.out.println(center+level);
		return "()";


	}








	//all helper functions.(nota part of the algorithm)
	private static String oppositeDirection(String dir){
		if(dir=="NORTH")return "SOUTH";
		else if(dir=="EAST")return "WEST";
		else if(dir=="SOUTH")return "NORTH";
		return "EAST";

	}



	public static int findNumberOfNodes(List<List<Integer>> tree, int parent, int child , List<Integer> seen){
		if(seen.get(child)==1)return 0;
		seen.set(child, 1);
		//System.out.println("-----------------------------------");

		//System.out.println(index++ +" " +parent +": its children "+tree.get(child));
		if(tree.get(child).size() <=1){
			return 1;
		}
		int count=0;
		//System.out.println("visited: "+ child);
		for(int val=0; val<tree.get(child).size(); val++){
			if(parent==tree.get(child).get(val))continue;

			count = count+ findNumberOfNodes(tree, child, tree.get(child).get(val), seen);
		}
		return count+1;
	}




	public static List<Integer> findTheCenterOfTheTree(List<List<Integer>>tree, HashMap<String, Integer>  nodeIndex) {
			final int n = tree.size();
			int[] degree = new int[n];

			// Find all leaf nodes
			List<Integer> leaves = new ArrayList<>();
			for (int i = 0; i < n; i++) {
				List<Integer> edges = tree.get(i);
				degree[i] = edges.size();
				if (degree[i] <= 1) {
					leaves.add(i);
					degree[i] = 0;
				}
			}

			int processedLeafs = leaves.size();

			// Remove leaf nodes and decrease the degree of each node adding new leaf nodes progressively
			// until only the centers remain.
			while (processedLeafs < n) {
				List<Integer> newLeaves = new ArrayList<>();
				for (int node : leaves) {
					for (int neighbor : tree.get(node)) {
						if (--degree[neighbor] == 1) {
							newLeaves.add(neighbor);
						}
					}
					degree[node] = 0;
				}
				processedLeafs += newLeaves.size();
				leaves = newLeaves;
			}

			return leaves;


	}



}
