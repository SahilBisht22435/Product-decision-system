
import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import {
  CATEGORIES,
  formatINR,
  type Category,
  type Product,
} from "@/lib/products";
import { searchProducts, fetchBrands } from "@/lib/api";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "SpecDuel — Search and Compare Products" },
      {
        name: "description",
        content:
          "Search laptops, smartphones and cars according to your requirements and compare two products side by side.",
      },
      {
        property: "og:title",
        content: "SpecDuel — Search and Compare Products",
      },
      {
        property: "og:description",
        content:
          "Find products based on your budget and preferences, then compare their specifications and evidence.",
      },
    ],
  }),
  component: Index,
});

function Index() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [brands, setBrands] = useState<string[]>([]);
  const [brandsLoading, setBrandsLoading] = useState(false);
  // Search and filter inputs
  const [cat, setCat] = useState<Category | "all">("all");
  const [q, setQ] = useState("");
  const [brand, setBrand] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");

  const [hasSearched, setHasSearched] = useState(false);
  const [picked, setPicked] = useState<string[]>([]);

  // Fetch available brands whenever the category changes.
useEffect(() => {
  let active = true;

  const loadBrands = async () => {
    setBrandsLoading(true);

    try {
      const data = await fetchBrands(cat);

      if (active) {
        setBrands(data);
      }
    } catch (err) {
      if (active) {
        setBrands([]);
        console.error("Failed to load brands:", err);
      }
    } finally {
      if (active) {
        setBrandsLoading(false);
      }
    }
  };

  loadBrands();

  return () => {
    active = false;
  };
}, [cat]);
  // Identify the category of the first selected product.
  const pickedCat = products.find(
    (p) => p.id === picked[0]
  )?.category;

  // Search products through the Spring Boot backend.
  const handleSearch = async (
    e: React.FormEvent<HTMLFormElement>
  ) => {
    e.preventDefault();

    if (
      (minPrice && (!Number.isFinite(Number(minPrice)) || Number(minPrice) < 0)) ||
      (maxPrice && (!Number.isFinite(Number(maxPrice)) || Number(maxPrice) < 0))
    ) {
      setError("Please enter valid, non-negative prices.");
      return;
    }

    if (
      minPrice &&
      maxPrice &&
      Number(minPrice) > Number(maxPrice)
    ) {
      setError("Minimum price cannot be greater than maximum price.");
      return;
    }

    setError("");
    setLoading(true);
    setPicked([]);

    try {
      const results = await searchProducts({
        category: cat,
        keyword: q.trim(),
        brand: brand.trim(),
        minPrice,
        maxPrice,
      });

      setProducts(results);
      setHasSearched(true);
    } catch (err: unknown) {
      setProducts([]);
      setHasSearched(false);

      setError(
        err instanceof Error
          ? err.message
          : "Failed to search products. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  // Reset search inputs and results.
  const clearFilters = () => {
    setCat("all");
    setQ("");
    setBrand("");
    setMinPrice("");
    setMaxPrice("");
    setProducts([]);
    setHasSearched(false);
    setPicked([]);
    setError("");
  };

  // Select or remove a product for comparison.
  const toggle = (id: string) => {
    setPicked((current) =>
      current.includes(id)
        ? current.filter((x) => x !== id)
        : current.length >= 2
          ? [current[1]!, id]
          : [...current, id]
    );
  };

  return (
    <div className="mx-auto max-w-6xl px-5 pb-32 pt-10">
      {/* Header */}
      <header className="mb-10 text-center">
        <p className="font-display text-sm uppercase tracking-[0.3em] text-primary">
          SpecDuel
        </p>

        <h1 className="mt-3 text-4xl font-bold leading-tight md:text-6xl">
          Find what you need.{" "}
          <span className="text-primary">
            Compare what matters.
          </span>
        </h1>

        <p className="mx-auto mt-4 max-w-2xl text-muted-foreground">
          Search for laptops, smartphones or cars based on your
          budget and preferences. Choose any two products from the
          same category to compare their specifications.
        </p>
      </header>

      {/* Search and filters */}
      <form
        onSubmit={handleSearch}
        className="mb-10 rounded-2xl border bg-card p-5 md:p-7"
      >
        <h2 className="mb-5 text-xl font-semibold">
          Search products
        </h2>

        {/* Category selection */}
        <label className="mb-3 block text-sm font-medium">
          Select category
        </label>

        <div className="mb-6 flex flex-wrap gap-2">
          {[{ id: "all", label: "All Categories" } as const, ...CATEGORIES].map(
            (c) => (
              <button
                key={c.id}
                type="button"
                onClick={() => {
                            setCat(c.id);
                            setBrand("");
                          }}
                className={`rounded-full border px-4 py-2 text-sm transition ${
                  cat === c.id
                    ? "border-primary bg-primary text-primary-foreground"
                    : "bg-background hover:border-primary"
                }`}
              >
                {c.label}
              </button>
            )
          )}
        </div>

        {/* Product keyword */}
        <div className="mb-5">
          <label className="mb-2 block text-sm font-medium">
            Product name or keyword
          </label>

          <input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Example: HP Pavilion, Samsung, Lenovo..."
            className="w-full rounded-lg border bg-background px-4 py-3 text-sm outline-none focus:border-primary"
          />
        </div>

        {/* Budget filters */}
        <div className="mb-5">
          <label className="mb-2 block text-sm font-medium">
            Your budget (₹)
          </label>

          <div className="grid gap-3 sm:grid-cols-2">
            <input
              type="number"
              min="0"
              step="any"
              value={minPrice}
              onChange={(e) => setMinPrice(e.target.value)}
              placeholder="Minimum price"
              className="w-full rounded-lg border bg-background px-4 py-3 text-sm outline-none focus:border-primary"
            />

            <input
              type="number"
              min="0"
              step="any"
              value={maxPrice}
              onChange={(e) => setMaxPrice(e.target.value)}
              placeholder="Maximum price"
              className="w-full rounded-lg border bg-background px-4 py-3 text-sm outline-none focus:border-primary"
            />
          </div>
        </div>

        {/* Brand filter */}
        <div className="mb-6">
          <label className="mb-2 block text-sm font-medium">
            Brand
          </label>

          <select
            value={brand}
            onChange={(e) => setBrand(e.target.value)}
            disabled={brandsLoading}
            className="w-full rounded-lg border bg-background px-4 py-3 text-sm outline-none focus:border-primary disabled:opacity-50"
          >
            <option value="">
              {brandsLoading ? "Loading brands..." : "All brands"}
            </option>

            {brands.map((b) => (
              <option key={b} value={b}>
                {b}
              </option>
            ))}
          </select>
        </div>

        {/* Search buttons */}
        <div className="flex flex-wrap gap-3">
          <button
            type="submit"
            disabled={loading}
            className="rounded-full bg-primary px-7 py-3 text-sm font-semibold text-primary-foreground transition hover:opacity-90 disabled:opacity-50"
          >
            {loading ? "Searching..." : "Search products →"}
          </button>

          <button
            type="button"
            onClick={clearFilters}
            disabled={loading}
            className="rounded-full border px-6 py-3 text-sm font-medium transition hover:border-primary disabled:opacity-50"
          >
            Clear filters
          </button>
        </div>
      </form>

      {/* Loading message */}
      {loading && (
        <p className="mb-6 text-center text-sm text-muted-foreground">
          Searching products from the PEDS backend...
        </p>
      )}

      {/* Error message */}
      {!loading && error && (
        <p className="mb-6 rounded-lg border border-destructive p-4 text-sm text-destructive">
          {error}
        </p>
      )}

      {/* Initial state */}
      {!loading && !error && !hasSearched && (
        <div className="rounded-2xl border border-dashed p-10 text-center">
          <h2 className="text-xl font-semibold">
            Start your product search
          </h2>

          <p className="mx-auto mt-2 max-w-lg text-sm text-muted-foreground">
            Select a category, enter your budget and choose your
            preferred brand. Your matching products will appear here
            after you search.
          </p>
        </div>
      )}

      {/* Search results */}
      {!loading && hasSearched && (
        <section>
          <div className="mb-5 flex flex-wrap items-center justify-between gap-2">
            <div>
              <h2 className="text-2xl font-semibold">
                Search results
              </h2>

              <p className="mt-1 text-sm text-muted-foreground">
                {products.length} matching product
                {products.length !== 1 ? "s" : ""} found
              </p>
            </div>
          </div>

          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {products.map((p) => {
              const selected = picked.includes(p.id);

              const disabled =
                !selected &&
                !!pickedCat &&
                picked.length > 0 &&
                pickedCat !== p.category;

              return (
                <article
                  key={p.id}
                  className={`flex flex-col rounded-xl border bg-card p-5 transition ${
                    selected
                      ? "border-primary"
                      : "hover:border-muted-foreground"
                  } ${disabled ? "opacity-40" : ""}`}
                >
                  <div className="flex items-start justify-between">
                    <span className="rounded-full bg-secondary px-2.5 py-0.5 text-xs capitalize text-muted-foreground">
                      {p.category}
                    </span>

                    <span className="text-sm text-muted-foreground">
                      Product
                    </span>
                  </div>

                  <h3 className="mt-3 text-lg font-semibold">
                    {p.name}
                  </h3>

                  <p className="text-sm text-muted-foreground">
                    {p.brand}
                  </p>

                  <ul className="mt-4 space-y-1 text-sm text-muted-foreground">
                    {Object.entries(p.specs)
                      .slice(0, 3)
                      .map(([key, value]) => (
                        <li key={key}>
                          <span className="text-foreground">
                            {key}:
                          </span>{" "}
                          {value}
                        </li>
                      ))}
                  </ul>

                  <div className="mt-auto flex items-center justify-between pt-5">
                    <span className="font-display text-xl font-semibold">
                      {formatINR(p.price)}
                    </span>

                    <button
                      type="button"
                      disabled={disabled}
                      onClick={() => toggle(p.id)}
                      className={`rounded-full px-4 py-1.5 text-sm font-medium ${
                        selected
                          ? "bg-primary text-primary-foreground"
                          : "border hover:border-primary"
                      }`}
                    >
                      {selected ? "✓ Selected" : "Compare"}
                    </button>
                  </div>
                </article>
              );
            })}
          </div>

          {products.length === 0 && (
            <div className="rounded-xl border border-dashed p-8 text-center">
              <h3 className="font-semibold">
                No matching products found
              </h3>

              <p className="mt-2 text-sm text-muted-foreground">
                Try changing your search keyword, budget or brand.
              </p>
            </div>
          )}
        </section>
      )}

      {/* Comparison selection bar */}
      {picked.length > 0 && (
        <div className="fixed inset-x-0 bottom-0 border-t bg-card/95 backdrop-blur">
          <div className="mx-auto flex max-w-6xl items-center gap-3 px-5 py-4">
            <p className="flex-1 text-sm">
              {picked
                .map(
                  (id) =>
                    products.find((p) => p.id === id)?.name ?? id
                )
                .join(" vs ")}

              {picked.length === 1 && (
                <span className="text-muted-foreground">
                  {" "}— select one more product
                </span>
              )}
            </p>

            <button
              type="button"
              onClick={() => setPicked([])}
              className="text-sm text-muted-foreground hover:text-foreground"
            >
              Clear
            </button>

            {picked.length === 2 && (
              <Link
                to="/compare"
                search={{
                  a: picked[0] ?? "",
                  b: picked[1] ?? "",
                }}
                className="rounded-full bg-primary px-5 py-2 text-sm font-semibold text-primary-foreground"
              >
                Compare now →
              </Link>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
