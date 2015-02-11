package org.usfirst.frc.team498.robot;

import java.util.List;


public class LightParser {
	public LightPiece[] Pieces;
	public LightParser(String file) {
		String[] lines = file.split("\n");
		LightPiece[] lightPieces = new LightPiece[lines.length];
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			String[] linePieces = line.split(" ");
			LightPiece piece = new LightPiece();
			piece.Light1 = linePieces[0].equals("1");
			piece.Light2 = linePieces[1].equals("1");
			piece.Light3 = linePieces[2].equals("1");
			piece.Light4 = linePieces[3].equals("1");
			piece.Duration = Integer.parseInt(linePieces[4]);
			lightPieces[i] = piece;
		}
		Pieces = lightPieces;
	}
	public LightParser(List<String> file) {
		
		LightPiece[] lightPieces = new LightPiece[file.size()];
		for (int i = 0; i < file.size(); i++) {
			String line = file.get(i);
			String[] linePieces = line.split(" ");
			LightPiece piece = new LightPiece();
			piece.Light1 = linePieces[0].equals("1");
			piece.Light2 = linePieces[1].equals("1");
			piece.Light3 = linePieces[2].equals("1");
			piece.Light4 = linePieces[3].equals("1");
			piece.Duration = Integer.parseInt(linePieces[4]);
			lightPieces[i] = piece;
		}
		Pieces = lightPieces;
	}
}
