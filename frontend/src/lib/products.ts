export type Category = "phone" | "laptop" | "car";

export interface Product {
  id: string;
  name: string;
  brand: string;
  category: Category;
  price: number;
  rating: number;
  unique: string;
  specs: Record<string, string>;
  scores: {
    performance: number;
    battery: number;
    display: number;
    build: number;
  };

  suitabilityScore?: number;
reasons?: string[];
evidenceConfidenceScore?: number;
overallScore?: number;
}

export const CATEGORIES: {
  id: Category;
  label: string;
  midMin: number;
  midMax: number;
}[] = [
  { id: "laptop", label: "Laptops", midMin: 30000, midMax: 80000 },
  { id: "phone", label: "Smartphones", midMin: 10000, midMax: 50000 },
  { id: "car", label: "Cars", midMin: 400000, midMax: 1500000 },
];

// Temporary mock-data helper retained for compatibility.
// Real product data is loaded from the Spring Boot API.
const p = (
  id: string,
  name: string,
  brand: string,
  category: Category,
  price: number,
  rating: number,
  unique: string,
  specs: Record<string, string>,
  s: [number, number, number, number],
): Product => ({
  id,
  name,
  brand,
  category,
  price,
  rating,
  unique,
  specs,
  scores: {
    performance: s[0],
    battery: s[1],
    display: s[2],
    build: s[3],
  },
});

// Keep an empty array so existing imports continue to compile.
// The homepage should use products returned by fetchProducts().
export const PRODUCTS: Product[] = [];

/**
 * Temporary UI score.
 * Do not treat this score as an evidence-based product recommendation
 * until the backend scoring model is connected.
 */
export function overallScore(
  prod: Product,
  pool: Product[] = PRODUCTS,
): number {
  const peers = pool.filter((x) => x.category === prod.category);
  const prices = peers.map((x) => x.price);

  const min = prices.length ? Math.min(...prices) : prod.price;
  const max = prices.length ? Math.max(...prices) : prod.price;

  const value =
    max === min
      ? 10
      : 10 - ((prod.price - min) / (max - min)) * 10;

  const s = prod.scores;
  const specAvg =
    (s.performance + s.battery + s.display + s.build) / 4;

  const total =
    specAvg * 0.5 +
    prod.rating * 2 * 0.25 +
    value * 0.25;

  return Math.round(total * 10);
}

export function bestOf(list: Product[]): Product | undefined {
  if (list.length === 0) return undefined;

  return [...list].sort(
    (a, b) => overallScore(b, list) - overallScore(a, list),
  )[0];
}

export const formatINR = (n: number) =>
  new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(n);
