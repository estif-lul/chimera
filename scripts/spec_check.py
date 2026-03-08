#!/usr/bin/env python3
"""Lightweight spec-to-code consistency checks for Project Chimera."""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import re
import sys


ROOT = Path(__file__).resolve().parents[1]


@dataclass(frozen=True)
class CheckFailure:
    message: str


REQUIRED_SPEC_FILES = [
    Path("specs/_meta.md"),
    Path("specs/technical.md"),
    Path("specs/openclaw_integration.md"),
    Path("specs/001-autonomous-influencer-network/spec.md"),
    Path("specs/001-autonomous-influencer-network/contracts/chimera-control-plane.openapi.yaml"),
]

REQUIRED_OPENAPI_PATHS = [
    "/api/v1/auth/local/login",
    "/api/v1/auth/session",
    "/api/v1/agents",
    "/api/v1/agents/{agentId}",
    "/api/v1/agents/{agentId}/memory-writebacks",
    "/api/v1/campaigns",
    "/api/v1/campaigns/{campaignId}/plan",
    "/api/v1/campaigns/{campaignId}/approve",
    "/api/v1/review-items",
    "/api/v1/review-items/{reviewItemId}/decisions",
    "/api/v1/agents/{agentId}/wallet",
    "/api/v1/wallets/{walletId}/transactions",
    "/api/v1/signals",
]

REQUIRED_CONTROLLERS = [
    Path("backend/src/main/java/com/chimera/controller/AuthController.java"),
    Path("backend/src/main/java/com/chimera/controller/AgentController.java"),
    Path("backend/src/main/java/com/chimera/controller/CampaignController.java"),
    Path("backend/src/main/java/com/chimera/controller/ReviewController.java"),
    Path("backend/src/main/java/com/chimera/controller/WalletController.java"),
    Path("backend/src/main/java/com/chimera/controller/SignalController.java"),
]

REQUIRED_CONTRACT_TESTS = [
    Path("backend/src/test/java/com/chimera/contract/AgentMemoryContractTest.java"),
    Path("backend/src/test/java/com/chimera/contract/CampaignContractTest.java"),
    Path("backend/src/test/java/com/chimera/contract/ReviewContractTest.java"),
    Path("backend/src/test/java/com/chimera/contract/SignalContractTest.java"),
    Path("backend/src/test/java/com/chimera/contract/WalletContractTest.java"),
]


def check_paths_exist(paths: list[Path], failures: list[CheckFailure], category: str) -> None:
    for rel_path in paths:
        abs_path = ROOT / rel_path
        if not abs_path.exists():
            failures.append(CheckFailure(f"[{category}] missing: {rel_path}"))


def check_openapi_paths(failures: list[CheckFailure]) -> None:
    openapi_path = ROOT / "specs/001-autonomous-influencer-network/contracts/chimera-control-plane.openapi.yaml"
    if not openapi_path.exists():
        failures.append(CheckFailure("[openapi] contract file missing"))
        return

    content = openapi_path.read_text(encoding="utf-8")
    for required_path in REQUIRED_OPENAPI_PATHS:
        # Anchor to YAML path keys, e.g. "  /api/v1/agents:".
        pattern = rf"^\s*{re.escape(required_path)}:\s*$"
        if not re.search(pattern, content, flags=re.MULTILINE):
            failures.append(
                CheckFailure(f"[openapi] required API path not found in contract: {required_path}")
            )


def main() -> int:
    failures: list[CheckFailure] = []

    check_paths_exist(REQUIRED_SPEC_FILES, failures, "spec")
    check_openapi_paths(failures)
    check_paths_exist(REQUIRED_CONTROLLERS, failures, "backend")
    check_paths_exist(REQUIRED_CONTRACT_TESTS, failures, "tests")

    if failures:
        print("spec-check: FAIL")
        for failure in failures:
            print(f" - {failure.message}")
        return 1

    print("spec-check: PASS")
    print("All required spec, contract, controller, and contract-test checks passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
