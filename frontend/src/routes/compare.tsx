
import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import {
  formatINR,
  type Product,
} from "@/lib/products";
import { fetchProducts } from "@/lib/api";

export const Route = createFileRoute("/compare")({
  validateSearch: (s: Record<string, unknown>) => ({
    a: String(s["a"] ?? ""),
    b: String(s["b"] ?? ""),
  }),
  head: () => ({
    meta: [
      { title: "Compare side by side — SpecDuel" },
      {
        name: "description",
        content: "Side-by-side specification comparison.",
      },
      {
        property: "og:title",
        content: "Compare side by side — SpecDuel",
      },
      {
        property: "og:description",
        content: "Compare products and review their specifications.",
      },
    ],
  }),
  component: Compare,
});

function Compare() {
  const { a, b } = Route.useSearch();

  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let active = true;

    fetchProducts()
      .then((data) => {
        if (active) {
          setProducts(data);
          setError("");
        }
      })
      .catch((err: unknown) => {
        if (active) {
          setError(
            err instanceof Error
              ? err.message
              : "Failed to load products"
          );
        }
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => {
      active = false;
    };
  }, []);

  if (loading) {
    return (
      <div className="mx-auto max-w-xl px-5 py-20 text-center">
        Loading products for comparison...
      </div>
    );
  }

  if (error) {
    return (
      <div className="mx-auto max-w-xl px-5 py-20 text-center">
        <p className="text-destructive">
          Could not load products: {error}
        </p>
        <Link to="/" className="mt-4 inline-block text-primary">
          ← Back to products
        </Link>
      </div>
    );
  }

  const productAId = a.trim();
  const productBId = b.trim();

  const A = productAId
    ? products.find((p) => String(p.id).trim() === productAId)
    : undefined;

  const B = productBId
    ? products.find((p) => String(p.id).trim() === productBId)
    : undefined;

  if (!A || !B) {
    return (
      <div className="mx-auto max-w-xl px-5 py-20 text-center">
        <h1 className="text-xl font-semibold">
          Could not find both selected products
        </h1>

        <p className="mt-3 text-sm text-muted-foreground">
          Return to the product list, select two products, and click
          Compare now.
        </p>

        <p className="mt-3 break-all text-xs text-muted-foreground">
          Product A ID: {a || "(missing)"}
        </p>

        <p className="mt-1 break-all text-xs text-muted-foreground">
          Product B ID: {b || "(missing)"}
        </p>

        <Link to="/" className="mt-5 inline-block text-primary">
          ← Back to products
        </Link>
      </div>
    );
  }

  // Use recommendation scores calculated by the backend.
  const scoreA = A.suitabilityScore ?? 0;
  const scoreB = B.suitabilityScore ?? 0;

  // Higher suitability score wins. If tied, the cheaper product wins.
  const winner =
    scoreA === scoreB
      ? A.price <= B.price
        ? A
        : B
      : scoreA > scoreB
        ? A
        : B;

  const keys = Array.from(
  new Set([...Object.keys(A.specs), ...Object.keys(B.specs)])
).filter((key) => key !== "Dataset Rating");

  return (
    <div className="mx-auto max-w-5xl px-5 py-10">
      <Link
        to="/"
        className="text-sm text-muted-foreground hover:text-primary"
      >
        ← All products
      </Link>

      {/* Winner section */}
      <div className="mt-6 rounded-xl border border-primary bg-win p-6 text-center">
        <p className="text-xs uppercase tracking-widest text-primary">
          Best suitability score
        </p>

        <h1 className="mt-1 text-3xl font-bold md:text-4xl">
          {winner.name}
        </h1>

        <p className="mt-2 text-lg font-semibold text-primary">
          {winner.suitabilityScore ?? "—"}/100
        </p>

        <p className="mt-1 text-muted-foreground">
          {formatINR(winner.price)}
        </p>

        <p className="mt-3 text-sm text-muted-foreground">
          The winner is selected using the backend suitability score.
          If both products have the same score, the cheaper product wins.
        </p>
      </div>

      {/* Product score cards */}
      <div className="mt-8 grid grid-cols-1 gap-4 sm:grid-cols-2">
        {[A, B].map((p) => (
          <div
            key={p.id}
            className={`rounded-xl border bg-card p-5 ${
              p.id === winner.id ? "border-primary" : ""
            }`}
          >
            <p className="text-sm text-muted-foreground">
              {p.brand}
            </p>

            <h2 className="mt-1 text-xl font-semibold">
              {p.name}
            </h2>

            <p className="mt-4 text-sm text-muted-foreground">
              Suitability score
            </p>

            <p className="font-display text-3xl font-bold text-primary">
              {p.suitabilityScore ?? "—"}
              <span className="text-sm text-muted-foreground">/100</span>
            </p>

            <div className="mt-4 space-y-3 border-t pt-4">
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">
                  Evidence confidence
                </span>
                <span className="font-semibold">
                  {p.evidenceConfidenceScore ?? 0}/100
                </span>
              </div>

              <div className="h-2 overflow-hidden rounded-full bg-secondary">
                <div
                  className="h-full rounded-full bg-primary"
                  style={{
                    width: `${Math.min(
                      100,
                      Math.max(0, p.evidenceConfidenceScore ?? 0)
                    )}%`,
                  }}
                />
              </div>

              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">
                  Overall score
                </span>
                <span className="font-semibold">
                  {p.overallScore ?? "—"}/100
                </span>
              </div>

              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">
                  Evidence status
                </span>
                <span className="font-medium">
                  {p.evidenceConfidenceScore === 0
                    ? "No confidence points"
                    : "Evidence available"}
                </span>
              </div>
            </div>

            {/* Recommendation reasons */}
            {p.reasons && p.reasons.length > 0 && (
              <div className="mt-5 border-t pt-4">
                <h3 className="mb-2 text-sm font-semibold">
                  Why this score?
                </h3>

                <ul className="space-y-2 text-sm text-muted-foreground">
                  {p.reasons.map((reason, index) => (
                    <li key={index}>✓ {reason}</li>
                  ))}
                </ul>
              </div>
            )}

            <p className="mt-5 text-xl font-semibold">
              {formatINR(p.price)}
            </p>

            <p className="mt-1 text-sm text-muted-foreground">
              Dataset rating:{" "}
              {p.specs["Dataset Rating"]
                ? `${p.specs["Dataset Rating"]}/100`
                : "Not available"}
            </p>
          </div>
        ))}
      </div>

      {/* Specification comparison table */}
      <div className="mt-8 overflow-x-auto rounded-xl border">
        <table className="w-full text-sm">
          <thead className="bg-secondary">
            <tr>
              <th className="p-3 text-left text-muted-foreground">
                Specification
              </th>
              <th className="p-3 text-left">{A.name}</th>
              <th className="p-3 text-left">{B.name}</th>
            </tr>
          </thead>

          <tbody>
            <tr className="border-t">
              <td className="p-3 text-muted-foreground">
                Suitability Score
              </td>
              <td className="p-3 font-semibold">
                {A.suitabilityScore ?? "—"}/100
              </td>
              <td className="p-3 font-semibold">
                {B.suitabilityScore ?? "—"}/100
              </td>
            </tr>

            <tr className="border-t">
              <td className="p-3 text-muted-foreground">
                Evidence Confidence
              </td>
              <td className="p-3">
                {A.evidenceConfidenceScore ?? 0}/100
              </td>
              <td className="p-3">
                {B.evidenceConfidenceScore ?? 0}/100
              </td>
            </tr>

            <tr className="border-t">
              <td className="p-3 text-muted-foreground">
                Overall Score
              </td>
              <td className="p-3">
                {A.overallScore ?? "—"}/100
              </td>
              <td className="p-3">
                {B.overallScore ?? "—"}/100
              </td>
            </tr>

            <tr className="border-t">
              <td className="p-3 text-muted-foreground">Price</td>
              <td
                className={`p-3 ${
                  A.price < B.price ? "bg-win text-primary" : ""
                }`}
              >
                {formatINR(A.price)}
              </td>
              <td
                className={`p-3 ${
                  B.price < A.price ? "bg-win text-primary" : ""
                }`}
              >
                {formatINR(B.price)}
              </td>
            </tr>

            <tr className="border-t">
              <td className="p-3 text-muted-foreground">
                Dataset Rating
              </td>
              <td className="p-3">
                {A.specs["Dataset Rating"]
                  ? `${A.specs["Dataset Rating"]}/100`
                  : "Not available"}
              </td>
              <td className="p-3">
                {B.specs["Dataset Rating"]
                  ? `${B.specs["Dataset Rating"]}/100`
                  : "Not available"}
              </td>
            </tr>

            {keys.map((key) => (
              <tr key={key} className="border-t">
                <td className="p-3 text-muted-foreground">{key}</td>
                <td className="p-3">{A.specs[key] ?? "—"}</td>
                <td className="p-3">{B.specs[key] ?? "—"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
