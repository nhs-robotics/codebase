import { useMemo } from 'react';
import { Canvas } from '@react-three/fiber';
import { OrbitControls, Box, Line } from '@react-three/drei';
import type { FieldPosition } from '../types/telemetry';
import { useTheme } from '../theme';

const FIELD_SIZE = 144; // inches
const TILE_SIZE = 24;   // 24" tiles
const TILES = FIELD_SIZE / TILE_SIZE; // 6

interface Props {
  fieldPosition: FieldPosition | null;
}

interface FieldGridProps {
  gridColor: string;
  borderColor: string;
}

function FieldGrid({ gridColor, borderColor }: FieldGridProps) {
  const lines = useMemo(() => {
    const result: Array<[[number, number, number], [number, number, number]]> = [];
    for (let i = 0; i <= TILES; i++) {
      const pos = -FIELD_SIZE / 2 + i * TILE_SIZE;
      result.push([[pos, 0, -FIELD_SIZE / 2], [pos, 0, FIELD_SIZE / 2]]);
      result.push([[-FIELD_SIZE / 2, 0, pos], [FIELD_SIZE / 2, 0, pos]]);
    }
    return result;
  }, []);

  return (
    <>
      {lines.map((pts, i) => (
        <Line key={i} points={pts} color={gridColor} lineWidth={1} />
      ))}
      {/* Border */}
      <Line
        points={[
          [-FIELD_SIZE / 2, 0.5, -FIELD_SIZE / 2],
          [FIELD_SIZE / 2, 0.5, -FIELD_SIZE / 2],
          [FIELD_SIZE / 2, 0.5, FIELD_SIZE / 2],
          [-FIELD_SIZE / 2, 0.5, FIELD_SIZE / 2],
          [-FIELD_SIZE / 2, 0.5, -FIELD_SIZE / 2],
        ]}
        color={borderColor}
        lineWidth={2}
      />
    </>
  );
}

interface RobotProps {
  position: FieldPosition;
  robotColor: string;
  coneColor: string;
}

function Robot({ position, robotColor, coneColor }: RobotProps) {
  const ROBOT_SIZE = 18;
  // FTC: x=right, y=up-field. Three.js: x=right, z=-forward
  const tx = position.x;
  const tz = -position.y;
  // FTC direction: CCW from +x (right). Three.js rotation around Y: CCW from +x when viewed from above
  const ry = position.direction;

  return (
    <group position={[tx, ROBOT_SIZE / 2, tz]} rotation={[0, ry, 0]}>
      <Box args={[ROBOT_SIZE, ROBOT_SIZE, ROBOT_SIZE]}>
        <meshStandardMaterial color={robotColor} />
      </Box>
      {/* Direction cone pointing in robot's forward (+x local) direction */}
      <mesh position={[ROBOT_SIZE * 0.75, 0, 0]} rotation={[0, 0, -Math.PI / 2]}>
        <coneGeometry args={[4, 14, 8]} />
        <meshStandardMaterial color={coneColor} />
      </mesh>
    </group>
  );
}

export function FieldViewer3D({ fieldPosition }: Props) {
  const { theme } = useTheme();

  return (
    <div style={{ background: theme.bgField, borderRadius: 4, overflow: 'hidden' }}>
      <Canvas camera={{ position: [0, 220, 80], fov: 45 }}>
        <ambientLight intensity={0.6} />
        <directionalLight position={[100, 200, 100]} intensity={1} />
        {/* Field floor */}
        <mesh rotation={[-Math.PI / 2, 0, 0]} position={[0, -0.5, 0]}>
          <planeGeometry args={[FIELD_SIZE, FIELD_SIZE]} />
          <meshStandardMaterial color={theme.color3dFloor} />
        </mesh>
        <FieldGrid gridColor={theme.color3dGrid} borderColor={theme.color3dBorder} />
        {fieldPosition && <Robot position={fieldPosition} robotColor={theme.color3dRobot} coneColor={theme.color3dCone} />}
        <OrbitControls target={[0, 0, 0]} />
      </Canvas>
    </div>
  );
}
