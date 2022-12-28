package ch.thoenluk.solvers.challenge22;

import ch.thoenluk.ChristmasSaver;
import ch.thoenluk.solvers.challenge22.why.dont.you.just.Side;
import ch.thoenluk.ut.Position;
import ch.thoenluk.ut.UtParsing;
import ch.thoenluk.ut.UtStrings;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HashMapLessThanPositionCommaMonkeyGreaterThan implements ChristmasSaver {

    public static final char AIR = '.';
    public static final char WALL = '#';


    @Override
    public String saveChristmas(String input) {
        final String[] parts = UtStrings.splitStringWithEmptyLines(input);
        final BoardGenerationResults boardGenerationResults = createBoard(parts[0]);
        final Map<Position, Character> board = boardGenerationResults.board();
        final List<Instruction> instructions = createInstructions(parts[1]);

        Position location = boardGenerationResults.start();
        Direction facing = Direction.RIGHT;

        for (Instruction instruction : instructions) {
            if (instruction == Instruction.LEFT_TURN) {
                facing = facing.leftTurn();
            }
            else if (instruction == Instruction.RIGHT_TURN) {
                facing = facing.rightTurn();
            }
            else {
                final Position next = getNextPositionOnBoard(location, facing, board);

                if (board.get(next) == AIR) {
                    location = next;
                }
            }
        }

        return Integer.toString(1000 * location.y() + 4 * location.x() + facing.ordinal());
    }

    @Override
    public String saveChristmasAgain(String input) {
        final String[] parts = UtStrings.splitStringWithEmptyLines(input);

        Side activeSide = Side.createDieFromDescription(parts[0]);
        final List<Instruction> instructions = createInstructions(parts[1]);

        Position location = new Position(0, 0);
        Direction facing = Direction.RIGHT;

        for (Instruction instruction : instructions) {
            if (instruction == Instruction.LEFT_TURN) {
                facing = facing.leftTurn();
            }
            else if (instruction == Instruction.RIGHT_TURN) {
                facing = facing.rightTurn();
            }
            else {
                final Side.MovementResult movementResult = activeSide.moveInDirection(location, facing);
                location = movementResult.location();
                activeSide = movementResult.side();
                facing = movementResult.facing();
            }
        }

        final Position locationOnFlatInput = location.offsetBy(activeSide.getTopLeft()).offsetBy(new Position(1, 1));
        return Integer.toString(1000 * locationOnFlatInput.y() + 4 * locationOnFlatInput.x() + facing.ordinal());
    }

    private BoardGenerationResults createBoard(String boardDescription) {
        final String[] lines = UtStrings.splitMultilineString(boardDescription);
        final Map<Position, Character> board = new HashMap<>();
        Position start = null;

        for (int y = 0; y < lines.length; y++) {
            final String line = lines[y];
            for (int x = 0; x < line.length(); x++) {
                final char terrain = line.charAt(x);
                if (terrain == AIR || terrain == WALL) {
                    board.put(new Position(y + 1, x + 1), terrain);

                    if (start == null) {
                        start = new Position(y + 1, x + 1);
                    }
                }
            }
        }

        return new BoardGenerationResults(board, start);
    }

    private List<Instruction> createInstructions(String instructionDescription) {
        final String separated = instructionDescription.replaceAll("L", ",L,").replaceAll("R", ",R,");
        final String[] split = UtStrings.splitCommaSeparatedString(separated);
        final List<Instruction> instructions = new LinkedList<>();

        for (String chunk : split) {
            if (chunk.equals("L")) {
                instructions.add(Instruction.LEFT_TURN);
            }
            else if (chunk.equals("R")) {
                instructions.add(Instruction.RIGHT_TURN);
            }
            else {
                final int distance = UtParsing.cachedParseInt(chunk);

                for (int i = 0; i < distance; i++) {
                    instructions.add(Instruction.FORWARD);
                }
            }
        }

        return instructions;
    }

    private Position getNextPositionOnBoard(Position location, Direction facing, Map<Position, Character> board) {
        final Position directNext = location.offsetBy(facing.getOffset());

        if (board.containsKey(directNext)) {
            return directNext;
        }

        final Direction turned = facing.flip();
        Position onBoard = location;
        Position next = location.offsetBy(turned.getOffset());

        while (board.containsKey(next)) {
            onBoard = next;
            next = next.offsetBy(turned.getOffset());
        }

        return onBoard;
    }

    public enum Direction {
        RIGHT(new Position(0, 1)),
        DOWN(new Position(1, 0)),
        LEFT(new Position(0, -1)),
        UP(new Position(-1, 0));

        private final Position offset;

        Direction(Position offset) {
            this.offset = offset;
        }

        public Position getOffset() {
            return offset;
        }

        public Direction leftTurn() {
            return getRelativeElement(-1);
        }

        public Direction flip() {
            return getRelativeElement(2);
        }

        public Direction rightTurn() {
            return getRelativeElement(1);
        }

        private Direction getRelativeElement(int relativeIndex) {
            return values()[wrapOrdinal(ordinal() + relativeIndex)];
        }

        private int wrapOrdinal(int unwrapped) {
            final int numberOfValues = values().length;
            return (unwrapped + numberOfValues) % numberOfValues;
        }
    }

    private enum Instruction {
        FORWARD,
        LEFT_TURN,
        RIGHT_TURN
    }

    private record BoardGenerationResults(Map<Position, Character> board, Position start) {}
}
