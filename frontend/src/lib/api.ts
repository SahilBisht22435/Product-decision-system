
import type { Product, Category } from "./products";

const API_BASE_URL = "http://localhost:8080/api/products";

interface BackendProduct {
  id: string;
  name: string;
  brand: string;
  category: string;
  price: number;

  // Common specifications
  ram?: number;
  storage?: number;
  batteryHours?: number;
  weight?: number;
  screenSize?: number;
  cameraMP?: number;
  batteryMah?: number;
  fuelType?: string | null;
  transmission?: string | null;
  mileage?: number;
  seatingCapacity?: number;

  // Laptop specifications
  cpuBrand?: string;
  cpuFamily?: string;
  cpuModel?: string;
  cpuCores?: number;
  cpuThreads?: number;
  displayWidthPx?: number;
  displayHeightPx?: number;
  displaySizeInch?: number;
  gpuBrand?: string;
  gpuModel?: string;
  gpuVramGb?: number;
  gpuType?: string;
  operatingSystem?: string;
  warrantyYears?: number;

  rating?: number;
  dataSource?: string;
}

interface BackendScoredProduct {
  product: BackendProduct;
  suitabilityScore: number;
  reasons: string[];
  evidenceConfidenceScore: number;
  overallScore: number;
}

interface ProductSearchFilters {
  category: Category | "all";
  keyword: string;
  brand: string;
  minPrice: string;
  maxPrice: string;
}

// Converts backend product data into the frontend Product format.
function mapProduct(product: BackendProduct): Product {
  const categoryMap: Record<string, Category> = {
    LAPTOP: "laptop",
    SMARTPHONE: "phone",
    PHONE: "phone",
    CAR: "car",
  };

  const specs: Record<string, string> = {};

  // Common specifications
  if (product.ram) specs["RAM"] = `${product.ram} GB`;
  if (product.storage) specs["Storage"] = `${product.storage} GB`;
  if (product.batteryHours) {
    specs["Battery"] = `${product.batteryHours} hours`;
  }
  if (product.weight) specs["Weight"] = `${product.weight} kg`;
  if (product.screenSize) {
    specs["Screen"] = `${product.screenSize} inches`;
  }
  if (product.cameraMP) specs["Camera"] = `${product.cameraMP} MP`;
  if (product.batteryMah) {
    specs["Battery"] = `${product.batteryMah} mAh`;
  }
  if (product.fuelType) specs["Fuel"] = product.fuelType;
  if (product.transmission) {
    specs["Transmission"] = product.transmission;
  }
  if (product.mileage) {
    specs["Mileage"] = `${product.mileage} km/l`;
  }
  if (product.seatingCapacity) {
    specs["Seats"] = `${product.seatingCapacity}`;
  }

  // Laptop specifications
  if (product.cpuBrand) specs["CPU Brand"] = product.cpuBrand;
  if (product.cpuFamily) specs["CPU Family"] = product.cpuFamily;
  if (product.cpuModel) specs["CPU Model"] = product.cpuModel;

  if (product.cpuCores != null) {
    specs["CPU Cores"] = `${product.cpuCores}`;
  }

  if (product.cpuThreads != null) {
    specs["CPU Threads"] = `${product.cpuThreads}`;
  }

  if (product.displayWidthPx && product.displayHeightPx) {
    specs["Display Resolution"] =
      `${product.displayWidthPx} × ${product.displayHeightPx}`;
  }

  if (product.displaySizeInch) {
    specs["Display Size"] = `${product.displaySizeInch} inches`;
  }

  if (product.gpuBrand) specs["GPU Brand"] = product.gpuBrand;
  if (product.gpuModel) specs["GPU Model"] = product.gpuModel;

  if (product.gpuVramGb != null) {
    specs["GPU VRAM"] = `${product.gpuVramGb} GB`;
  }

  if (product.gpuType) specs["GPU Type"] = product.gpuType;

  if (product.operatingSystem) {
    specs["Operating System"] = product.operatingSystem;
  }

  if (product.warrantyYears != null) {
    specs["Warranty"] = `${product.warrantyYears} years`;
  }

  if (product.rating != null) {
    specs["Dataset Rating"] = `${product.rating}`;
  }

  return {
    id: product.id,
    name: product.name,
    brand: product.brand,
    category:
      categoryMap[product.category.toUpperCase()] ?? "laptop",
    price: product.price,
    rating: product.rating ?? 0,
    unique: "Product data supplied by PEDS backend",
    specs,
    scores: {
      performance: 0,
      battery: 0,
      display: 0,
      build: 0,
    },
  };
}

// Fetches scored products for the comparison page.
export async function fetchProducts(): Promise<Product[]> {
  const response = await fetch(`${API_BASE_URL}/scored`);

  if (!response.ok) {
    throw new Error("Failed to fetch scored products");
  }

  const items: BackendScoredProduct[] = await response.json();

  return items.map((item) => ({
    ...mapProduct(item.product),
    suitabilityScore: item.suitabilityScore,
    reasons: item.reasons,
    evidenceConfidenceScore: item.evidenceConfidenceScore,
    overallScore: item.overallScore,
  }));
}

// Searches products using the selected filters.
export async function searchProducts(
  filters: ProductSearchFilters
): Promise<Product[]> {
  const params = new URLSearchParams();

  // Convert frontend category names into backend category names.
  const categoryMap: Record<string, string> = {
    laptop: "LAPTOP",
    phone: "SMARTPHONE",
    car: "CAR",
  };

const backendCategory = categoryMap[filters.category];

if (backendCategory) {
  params.set("category", backendCategory);
}

  if (filters.keyword.trim()) {
    params.set("keyword", filters.keyword.trim());
  }

  if (filters.brand.trim()) {
    params.set("brand", filters.brand.trim());
  }

  if (filters.minPrice.trim()) {
    params.set("minPrice", filters.minPrice.trim());
  }

  if (filters.maxPrice.trim()) {
    params.set("maxPrice", filters.maxPrice.trim());
  }

  const response = await fetch(
    `${API_BASE_URL}/search?${params.toString()}`
  );

  if (!response.ok) {
    throw new Error("Failed to search products");
  }

  const items: BackendProduct[] = await response.json();

  return items.map(mapProduct);

  
}
// Fetches available brands from the backend.
export async function fetchBrands(
  category: Category | "all"
): Promise<string[]> {
  const params = new URLSearchParams();

  const categoryMap: Record<string, string> = {
    laptop: "LAPTOP",
    phone: "SMARTPHONE",
    car: "CAR",
  };

  if (category !== "all") {
    const backendCategory = categoryMap[category];

    if (backendCategory) {
      params.set("category", backendCategory);
    }
  }

  const response = await fetch(
    `${API_BASE_URL}/brands?${params.toString()}`
  );

  if (!response.ok) {
    throw new Error("Failed to fetch available brands");
  }

  return response.json();
}