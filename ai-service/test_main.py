import unittest
from main import EventFeatures, severity_value, to_vector
from datetime import datetime, timezone

class TestFeatures(unittest.TestCase):
    def make_event(self, severity="INFO"):
        return EventFeatures(event_type="login_success", severity=severity,
                             timestamp=datetime(2026, 1, 1, 12, tzinfo=timezone.utc),
                             source_ip="203.0.113.10", username="demo", message="ok")

    def test_vector_is_deterministic_and_numeric(self):
        a = to_vector(self.make_event())
        b = to_vector(self.make_event())
        self.assertEqual(a, b)
        self.assertEqual(len(a), 11)

    def test_severity_order(self):
        self.assertLess(severity_value("INFO"), severity_value("HIGH"))
        self.assertLess(severity_value("HIGH"), severity_value("CRITICAL"))

if __name__ == "__main__":
    unittest.main()
